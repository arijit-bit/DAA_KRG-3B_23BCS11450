class Solution {
  public:
    int minPlatform(vector<int>& arr, vector<int>& dep) {
        
        int n = arr.size();
        
        
        sort(arr.begin(), arr.end());
        sort(dep.begin(), dep.end());
        
        int platform = 1;      
        int maxPlatform = 1;   

        int i = 1;  
        int j = 0;  
        
        
        while (i < n && j < n) {
            if (arr[i] <= dep[j]) {
                platform++;
                i++;
            } else {
                platform--;
                j++;
            }
            maxPlatform = max(maxPlatform, platform);
        }
        
        return maxPlatform;
    }
};
