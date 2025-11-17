class Solution {
public:
    static const long long MOD = 1000000007;

    long long modPow(long long base, long long exp) {
        long long result = 1;
        base %= MOD;
        
        while (exp > 0) {
            if (exp & 1)
                result = (result * base) % MOD;
            base = (base * base) % MOD;
            exp >>= 1;
        }
        return result;
    }

    int monkeyMove(int n) {
        long long total = modPow(2, n); 
        long long ans = (total - 2 + MOD) % MOD;
        return ans;
    }
};
