package seedu.duke;

import seedu.duke.exceptions.DukeException;
import seedu.duke.exceptions.fluid.FluidExceptions;
import seedu.duke.exceptions.foodbank.FoodBankException;
import seedu.duke.exceptions.meal.MealException;
import seedu.duke.exceptions.schedule.GetActivityException;
import seedu.duke.exceptions.schedule.InvalidActivityFormatException;
import seedu.duke.exceptions.schedule.InvalidScheduleDescriptionException;
import seedu.duke.exceptions.schedule.MissingActivityQuantifierException;
import seedu.duke.exceptions.schedule.MissingActivitySplitterException;
import seedu.duke.exceptions.workout.MissingWorkoutCalorieSeparatorException;
import seedu.duke.exceptions.workout.NegativeWorkoutCalorieException;
import seedu.duke.exceptions.schedule.ScheduleException;
import seedu.duke.exceptions.workout.WorkoutException;
import seedu.duke.schedule.WorkoutActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Parser {

    public static final String DATE_SEPARATOR = " /d ";
    public static final String TIME_SEPARATOR = " /t ";
    public static final String CALORIE_SEPARATOR = " /c ";
    public static final String RECURRING_FLAG = " /r";
    public static final String VOLUME_SEPARATOR = " /v ";
    public static final String ACTIVITY_SEPARATOR = " /a ";
    public static final String MULTIPLE_ACTIVITY_MARKER = ",";
    public static final String ACTIVITY_SPLITTER = ":";
    public static final String QUANTIFIER_SPLITTER = "x";
    public static final String EMPTY_STRING = "";

    //@@author teoziyiivy
    public static boolean containsDateSeparator(String inputArguments) {
        return inputArguments.contains(DATE_SEPARATOR);
    }

    //@@author teoziyiivy
    public static boolean containsTimeSeparator(String inputArguments) {
        return inputArguments.contains(TIME_SEPARATOR);
    }

    //@@author teoziyiivy
    public static boolean containsCalorieSeparator(String inputArguments) {
        return inputArguments.contains(CALORIE_SEPARATOR);
    }

    //@@author EdwardZYWang
    public static boolean isRecurringWorkout(String inputArguments) {
        String[] splitResults = inputArguments.split(RECURRING_FLAG, 2);
        if (splitResults.length == 1) {
            return false;
        }
        return splitResults[1].isEmpty(); // true if /r flag is at the end of the string
    }

    //@@author teoziyiivy
    public static int parseStringToInteger(String input) throws NumberFormatException {
        return Integer.parseInt(input);
    }

    //@@author teoziyiivy
    public static double parseStringToDouble(String input) throws NumberFormatException {
        return Double.parseDouble(input);
    }

    //@@author VishalJeyaram
    /**
     * Checks if the user's input contains separators.
     *
     * @param inputArguments User's input.
     * @return true, if input contains separators, and false, if input does not contain separators.
     */
    public static boolean containsSeparators(String inputArguments) {
        if (inputArguments.contains(CALORIE_SEPARATOR.trim())) {
            return true;
        } else if (inputArguments.contains(DATE_SEPARATOR.trim())) {
            return true;
        } else if (inputArguments.contains(TIME_SEPARATOR.trim())) {
            return true;
        } else {
            return false;
        }
    }

    //@@author VishalJeyaram
    /**
     * Returns calories extracted from user's input.
     *
     * @param inputArguments User's input.
     * @return Calories.
     * @throws DukeException If the user's description is empty.
     * @throws NumberFormatException If calories is not an integer value.
     * @throws FoodBankException If food already exists within either meal or fluid library.
     */
    public static int getCalories(String inputArguments)
            throws DukeException, NumberFormatException, FoodBankException {
        int calories = 0;
        if (!containsCalorieSeparator(inputArguments)) {
            String description = getDescription(inputArguments);
            if (Parser.containsSeparators(description)) {
                throw new DukeException("");
            }
            calories = FoodBank.findCalories(description);
            return calories;
        } else {
            String[] userInput = inputArguments.split("\\s+");
            int length = userInput.length;
            for (int i = 1; i < length; i++) {
                if (userInput[i].equals(CALORIE_SEPARATOR.trim())) {
                    calories = parseStringToInteger(userInput[i + 1]);
                }
            }
            if (calories < 0) {
                throw new DukeException("Negative calories");
            } else {
                return calories;
            }
        }
    }

    //@@author EdwardZYWang
    public static int getCaloriesBurnedForWorkout(String inputArguments) throws WorkoutException {
        int calories = 0;
        boolean isCaloriesParsed = false;
        String[] userInput = inputArguments.split("\\s+");
        int length = userInput.length;
        for (int i = 0; i < length; i++) {
            if (userInput[i].equals(CALORIE_SEPARATOR.trim())) {
                try {
                    calories = parseStringToInteger(userInput[i + 1]);
                    isCaloriesParsed = true;
                    break;
                } catch (IndexOutOfBoundsException e) {
                    throw new NumberFormatException();
                }
            }
        }
        if (!isCaloriesParsed) {
            throw new MissingWorkoutCalorieSeparatorException();
        }
        if (calories < 0) {
            throw new NegativeWorkoutCalorieException();
        } else {
            return calories;
        }
    }

    /**
     * This method extracts volume parameter from user input.
     *
     *@param inputArguments user input provided
     *@throws DukeException if volume entered is negative
     *@return volume parameter
     *
     *@author pragyan01
     */
    public static int getVolume(String inputArguments) throws DukeException {
        String[] userInput = inputArguments.split("\\s+");
        int length = userInput.length;
        int volume = 0;
        for (int i = 1; i < length; i++) {
            if (userInput[i].equals(VOLUME_SEPARATOR.trim())) {
                volume = parseStringToInteger(userInput[i + 1]);
                break;
            }
        }
        if (volume < 0) {
            throw new DukeException("Negative volume");
        }
        return volume;
    }

    /**
     * This method extracts description parameter from user input.
     *
     *@param inputArguments user input provided
     *@return description parameter
     *
     *@author pragyan01
     */
    public static String getDescription(String inputArguments) {
        String[] userInput;
        if (containsCalorieSeparator(inputArguments)) {
            userInput = inputArguments.split(CALORIE_SEPARATOR);
        } else if (containsDateSeparator(inputArguments)) {
            userInput = inputArguments.split(DATE_SEPARATOR);
        } else if (containsTimeSeparator(inputArguments)) {
            userInput = inputArguments.split(TIME_SEPARATOR);
        } else {
            return inputArguments;
        }
        String description = userInput[0].trim();
        return description;
    }

    //@@author VishalJeyaram
    /**
     * Returns date extracted from user's input.
     *
     * @param inputArguments User's input.
     * @return Date.
     * @throws DateTimeParseException If the date is not entered properly.
     */
    public static String getDate(String inputArguments) throws DateTimeParseException {
        String[] userInput = inputArguments.split("\\s+");
        int length = userInput.length;
        String date = "";
        for (int i = 1; i < length; i++) {
            if (userInput[i].equals(DATE_SEPARATOR.trim())) {
                date = userInput[i + 1];
                break;
            }
        }
        if (date.equals("")) {
            String newDate = getSystemDate();
            DateTracker.checkIfDateExists(newDate);
            return newDate;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        DateTracker.checkIfDateExists(formatter.format(localDate));
        return formatter.format(localDate);
    }

    //@@author teoziyiivy
    public static String getDateNoDateTracker(String inputArguments) throws DateTimeParseException {
        String[] userInput = inputArguments.split("\\s+");
        int length = userInput.length;
        String date = EMPTY_STRING;
        for (int i = 1; i < length; i++) {
            if (userInput[i].equals(DATE_SEPARATOR.trim())) {
                date = userInput[i + 1];
                break;
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return formatter.format(localDate);
    }

    //@@author teoziyiivy
    public static String getTime(String inputArguments) throws DateTimeParseException {
        String[] userInput = inputArguments.split("\\s+");
        int length = userInput.length;
        String time = "";
        for (int i = 1; i < length; i++) {
            if (userInput[i].equals(TIME_SEPARATOR.trim())) {
                time = userInput[i + 1];
                break;
            }
        }
        if (time.equals("")) {
            time = getSystemTime();
        }
        LocalTime localTime = LocalTime.parse(time);
        String properTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        return properTime;
    }

    //@@author teoziyiivy
    /**
     * Extracts the weight from the user input.
     *
     * @param inputArguments User input.
     * @throws DukeException If input does not have a weight or (weight < 0) or (weight > 1000).
     */
    public static double getWeight(String inputArguments) throws DukeException {
        String[] userInput = inputArguments.split(DATE_SEPARATOR);
        if (!userInput[0].matches("^\\d+(\\.\\d+)?")) {
            throw new DukeException("Invalid weight!");
        }
        double weight = parseStringToDouble(userInput[0]);
        if (weight < 0) {
            throw new DukeException("Negative weight");
        }

        if (weight > 1000) {
            throw new DukeException("Exceeded maximum weight");
        }
        return weight;
    }

    //@@author arvejw
    /**
     * Returns the description of the scheduled workout.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @return String Description of the workout.
     * @throws ScheduleException If unable to extract description.
     */
    public static String getScheduleDescription(String inputArguments) throws ScheduleException {
        String[] userInput = inputArguments.split(DATE_SEPARATOR);
        if (userInput.length == 1) {
            throw new InvalidScheduleDescriptionException();
        }
        String description = userInput[0].trim();
        return description;
    }

    //@@author arvejw
    /**
     * Returns the description and arguments for the workout activity.
     *
     * @param inputArguments Arguments input by the user that come after the command word.
     * @return Map of activity description and activity quantifier pairs.
     * @throws ScheduleException If there is missing activity splitter, quantifier or invalid format.
     */
    public static Map<String, ArrayList<Integer>> getActivities(String inputArguments) throws ScheduleException {
        int indexOfActivitySeparator = inputArguments.indexOf(Parser.ACTIVITY_SEPARATOR);
        String subSubstringAfterActivitySeparator = EMPTY_STRING;
        if (indexOfActivitySeparator != -1) {
            subSubstringAfterActivitySeparator = inputArguments.substring(
                    indexOfActivitySeparator).trim();
            if (isRecurringWorkout(inputArguments)) {
                subSubstringAfterActivitySeparator = subSubstringAfterActivitySeparator
                        .replace(RECURRING_FLAG, "").replace(ACTIVITY_SEPARATOR.trim(), "").trim();
            } else {
                subSubstringAfterActivitySeparator = subSubstringAfterActivitySeparator
                        .replace(ACTIVITY_SEPARATOR.trim(), "").trim();
            }
        }
        if (subSubstringAfterActivitySeparator.isEmpty()) {
            return new HashMap<>();
        } else {
            return getActivityArguments(subSubstringAfterActivitySeparator.split(MULTIPLE_ACTIVITY_MARKER));
        }
    }

    //@@author arvejw
    /**
     * Returns the description and arguments for the workout activity.
     *
     * @param nonParsedActivities The activities which have not been parsed.
     * @return Map of activity description and activity quantifier pairs.
     * @throws ScheduleException If there is missing activity splitter, quantifier or invalid format.
     */
    public static Map<String, ArrayList<Integer>> getActivityArguments(String[] nonParsedActivities)
            throws ScheduleException {
        Map<String, ArrayList<Integer>> outputMap = new HashMap<>();
        for (String activity : nonParsedActivities) {
            String[] splitResults = activity.split(ACTIVITY_SPLITTER, 2);
            if (splitResults.length == 1) {
                throw new MissingActivitySplitterException();
            }
            String[] quantifierSplitResults = splitResults[1].split(QUANTIFIER_SPLITTER, 2);
            if (quantifierSplitResults.length == 1 && !WorkoutActivity.isDistanceActivity(splitResults[0])) {
                throw new MissingActivityQuantifierException();
            }
            ArrayList<Integer> activityQuantifiers = new ArrayList<Integer>();
            if (WorkoutActivity.isDistanceActivity(splitResults[0])) {
                parseDistanceActivityQuantifiers(quantifierSplitResults, activityQuantifiers);
            } else if (quantifierSplitResults.length == 2) {
                parseNonDistanceActivityQuantifiers(quantifierSplitResults, activityQuantifiers);
            } else {
                throw new GetActivityException();
            }
            outputMap.put(splitResults[0].trim(), activityQuantifiers);
        }
        return outputMap;
    }

    //@@author arvejw
    /**
     * Adds distance activity quantifiers to array list of activity quantifiers.
     *
     * @param quantifierSplitResults Array of quantifier split results.
     * @param activityQuantifiers ArrayList of activity quantifiers.
     * @throws InvalidActivityFormatException If non-integer or integer less than equal to 0 detected.
     */
    private static void parseDistanceActivityQuantifiers(
            String[] quantifierSplitResults, ArrayList<Integer> activityQuantifiers)
            throws InvalidActivityFormatException {
        try {
            if (parseStringToInteger(quantifierSplitResults[0].trim()) <= 0) {
                throw new NumberFormatException();
            }
            activityQuantifiers.add(parseStringToInteger(quantifierSplitResults[0].trim()));
        } catch (NumberFormatException e) {
            throw new InvalidActivityFormatException();
        }
    }

    //@@author arvejw
    /**
     * Adds non-distance activity quantifiers to array list of activity quantifiers.
     *
     * @param quantifierSplitResults Array of quantifier split results.
     * @param activityQuantifiers ArrayList of activity quantifiers.
     * @throws InvalidActivityFormatException If non-integer or integer less than equal to 0 detected.
     */
    private static void parseNonDistanceActivityQuantifiers(
            String[] quantifierSplitResults, ArrayList<Integer> activityQuantifiers)
            throws InvalidActivityFormatException {
        try {
            if (parseStringToInteger(quantifierSplitResults[0].trim()) <= 0
                    || parseStringToInteger(quantifierSplitResults[1].trim()) <= 0) {
                throw new NumberFormatException();
            }
            activityQuantifiers.add(parseStringToInteger(quantifierSplitResults[0].trim()));
            activityQuantifiers.add(parseStringToInteger(quantifierSplitResults[1].trim()));
        } catch (NumberFormatException e) {
            throw new InvalidActivityFormatException();
        }
    }

    /**
     * This method obtains current system date of the user.
     *
     *@return current system date
     *
     *@author pragyan01
     */
    public static String getSystemDate() {
        String systemDate = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        LocalDate now = LocalDate.now();
        systemDate = now.format(dtf);

        return systemDate;
    }

    /**
     * This method obtains current system time of the user.
     *
     *@return current system time
     *
     *@author pragyan01
     */
    public static String getSystemTime() {
        String systemTime = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        LocalTime now = LocalTime.now();
        systemTime = now.format(dtf);
        return systemTime;
    }
}
