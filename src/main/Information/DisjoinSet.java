package main.Information;

/**
 * Created by Dang Tien Son on 8/17/2017.
 */
public class DisjoinSet {
    public  Integer[] par = new Integer[1000];
    public  DisjoinSet(int n){
        for (int i=0;i<=n;i++) par[i]=i;
    }
    public int ans(int i){
        if (par[i]==i) return i;
        else {
            par[i]=ans(par[i]);
            return par[i];
        }
    }
    public  void join(int i,int j){
        par[ans(i)] = ans(j);
    }

}
