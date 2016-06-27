package cn.shnulaa.bar;

public class ProgressBar {
	public static String showBarByPoint(double currentPoint, double finishPoint, int barLength, long perSecond,
			boolean withR) {
		double rate = currentPoint / finishPoint;
		int barSign = (int) (rate * barLength);
		return makeBarBySignAndLength(barSign, barLength) + String.format(" %.2f%%", rate * 100)
				+ String.format(" %sK/S", perSecond) + (withR ? "\r" : "\n");
	}

	private static String makeBarBySignAndLength(int barSign, int barLength) {
		StringBuilder bar = new StringBuilder();
		bar.append("[");
		for (int i = 1; i <= barLength; i++) {
			if (i < barSign) {
				bar.append("=");
			} else if (i == barSign) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}
		bar.append("]");
		return bar.toString();
	}

	public static void main(String[] args) {

		for (int i = 0; i <= 100; i++) {
			System.out.println(showBarByPoint(i, 100, 10, i, true));
		}

	}

}
