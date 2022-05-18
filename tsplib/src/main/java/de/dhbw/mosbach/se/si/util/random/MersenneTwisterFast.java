package de.dhbw.mosbach.se.si.util.random;

import java.io.Serial;
import java.io.Serializable;

public class MersenneTwisterFast implements Serializable, Cloneable, RandomGenerator {
    
    @Serial
    private static final long serialVersionUID = -8219700664442619525L;

    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df;
    private static final int UPPER_MASK = 0x80000000;
    private static final int LOWER_MASK = 0x7fffffff;

    private static final int TEMPERING_MASK_B = 0x9d2c5680;
    private static final int TEMPERING_MASK_C = 0xefc60000;

    private int[] mt;
    private int mti;
    private int[] mag01;

    public MersenneTwisterFast(long seed) {
        setSeed(seed);
    }

    @Override
    public Object clone() {
        try {
            MersenneTwisterFast mersenneTwisterFast =
                (MersenneTwisterFast) (super.clone());
            mersenneTwisterFast.mt = mt.clone();
            mersenneTwisterFast.mag01 = mag01.clone();
            return mersenneTwisterFast;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override
    public RandomGenerator cloneWithSeed(long seed) {
        var mersenneTwisterFast = (MersenneTwisterFast) this.clone();
        mersenneTwisterFast.setSeed(seed);
        return mersenneTwisterFast;
    }

    public void setSeed(long seed) {
        mt = new int[N];

        mag01 = new int[2];
        mag01[1] = MATRIX_A;

        mt[0] = (int) (seed);
        for (mti = 1; mti < N; mti++) {
            mt[mti] =
                (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
        }
    }

    @Override
    public double nextDouble() {
        int y;
        int z;

        if (mti >= N) {
            int kk;
            final int[] mt = this.mt;
            final int[] mag01 = this.mag01;

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }

            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }

            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];
        y ^= y >>> 11;
        y ^= (y << 7) & TEMPERING_MASK_B;
        y ^= (y << 15) & TEMPERING_MASK_C;
        y ^= (y >>> 18);

        if (mti >= N) {
            int kk;
            final int[] mt = this.mt;
            final int[] mag01 = this.mag01;

            for (kk = 0; kk < N - M; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (z >>> 1) ^ mag01[z & 0x1];
            }

            for (; kk < N - 1; kk++) {
                z = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (z >>> 1) ^ mag01[z & 0x1];
            }

            z = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (z >>> 1) ^ mag01[z & 0x1];

            mti = 0;
        }

        z = mt[mti++];
        z ^= z >>> 11;
        z ^= (z << 7) & TEMPERING_MASK_B;
        z ^= (z << 15) & TEMPERING_MASK_C;
        z ^= (z >>> 18);

        return ((((long) (y >>> 6)) << 27) + (z >>> 5)) / (double) (1L << 53);
    }

    @Override
    public int nextInt(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive | " + n);

        if ((n & -n) == n) {
            int y;

            if (mti >= N) {
                int kk;
                final int[] mt = this.mt;
                final int[] mag01 = this.mag01;

                for (kk = 0; kk < N - M; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

                mti = 0;
            }

            y = mt[mti++];
            y ^= y >>> 11;
            y ^= (y << 7) & TEMPERING_MASK_B;
            y ^= (y << 15) & TEMPERING_MASK_C;
            y ^= (y >>> 18);

            return (int) ((n * (long) (y >>> 1)) >> 31);
        }

        int bits, val;
        do {
            int y;

            if (mti >= N) {
                int kk;
                final int[] mt = this.mt;
                final int[] mag01 = this.mag01;

                for (kk = 0; kk < N - M; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                for (; kk < N - 1; kk++) {
                    y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                    mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
                }
                y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
                mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

                mti = 0;
            }

            y = mt[mti++];
            y ^= y >>> 11;
            y ^= (y << 7) & TEMPERING_MASK_B;
            y ^= (y << 15) & TEMPERING_MASK_C;
            y ^= (y >>> 18);

            bits = (y >>> 1);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
}