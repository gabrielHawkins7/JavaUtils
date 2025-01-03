
//fast round float (https://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals)
public static float round2(float number, int scale) {
		int pow = 10;
		for (int i = 1; i < scale; i++)
			pow *= 10;
		float tmp = number * pow;
		return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}
