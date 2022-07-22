package example;

public class StringCalculator {
    /**
     * @apiNote text 값이 비어있으면 0을 반환, 비어있지 않으면 구분자로 분리(split), 숫자로 변환(toInts), 숫자의 합(sum)을 구한다.
     * @param text
     * @return
     */
    public int add(String text) {
        if (isBlank(text)) {
            return 0;
        }
        return sum(toInts(split(text)));
    }

    private int[] toInts(String[] values) {
        int[] numbers = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            numbers[i] = toPositive(values[i]);
        }
        return numbers;
    }

    private int sum(int[] numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    private boolean isBlank(String text) {

        return text == null || text.isEmpty();
    }

    private String[] split(String text) {
        return text.split(",");
    }

    private int toPositive(String value) {
        int number = Integer.parseInt(value);
        if (number < 0) {
            throw new RuntimeException();
        }
        return number;
    }
}
