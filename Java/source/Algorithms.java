import java.util.*;

/** Assignment 4: dynamic programming and string processing. */
public final class Algorithms {
    private Algorithms() {}

    static Map<String,Object> lcs(Map<String,Object> input) {
        String a = (String) input.get("a"), b = (String) input.get("b");
        int n = a.length(), m = b.length();
        int[][] dp = new int[n+1][m+1];
        for (int i=1;i<=n;i++) for(int j=1;j<=m;j++)
            dp[i][j] = a.charAt(i-1)==b.charAt(j-1) ? dp[i-1][j-1]+1 : Math.max(dp[i-1][j],dp[i][j-1]);
        StringBuilder s = new StringBuilder();
        int i=n,j=m;
        while(i>0&&j>0) {
            if(a.charAt(i-1)==b.charAt(j-1)){s.append(a.charAt(i-1));i--;j--;}
            else if(dp[i-1][j]>=dp[i][j-1]) i--;
            else j--;
        }
        return Map.of("length",dp[n][m],"sequence",s.reverse().toString());
    }

    static Map<String,Object> merge(Map<String,Object> input) {
        List<?> sizes=(List<?>)input.get("sizes");
        int n=sizes.size();
        if(n<=1) return Map.of("cost",0L,"plan",List.of());
        long[] pref=new long[n+1];
        for(int i=0;i<n;i++)pref[i+1]=pref[i]+((Number)sizes.get(i)).longValue();
        long[][] dp=new long[n][n];
        int[][] split=new int[n][n];
        for(int len=2;len<=n;len++)for(int l=0;l+len<=n;l++){
            int r=l+len-1;long best=Long.MAX_VALUE;
            for(int k=l;k<r;k++){
                long v=dp[l][k]+dp[k+1][r]+pref[r+1]-pref[l];
                if(v<best){best=v;split[l][r]=k;}
            }
            dp[l][r]=best;
        }
        List<List<Integer>> plan=new ArrayList<>();
        buildMerge(0,n-1,split,plan);
        return Map.of("cost",dp[0][n-1],"plan",plan);
    }
    private static void buildMerge(int l,int r,int[][] split,List<List<Integer>> plan){
        if(l>=r)return;
        int k=split[l][r];
        buildMerge(l,k,split,plan);
        buildMerge(k+1,r,split,plan);
        plan.add(List.of(l,k,r));
    }

    static Map<String,Object> tree(Map<String,Object> input) {
        int n=((Number)input.get("n")).intValue();
        if(n==0)return Map.of("weight",0L,"selected",List.of());
        List<?> weights=(List<?>)input.get("weights"), edges=(List<?>)input.get("edges");
        int[] degree=new int[n];
        for(Object obj:edges){List<?> e=(List<?>)obj;int u=((Number)e.get(0)).intValue(),v=((Number)e.get(1)).intValue();degree[u]++;degree[v]++;}
        int[][] adj=new int[n][];
        for(int v=0;v<n;v++)adj[v]=new int[degree[v]];
        Arrays.fill(degree,0);
        for(Object obj:edges){List<?> e=(List<?>)obj;int u=((Number)e.get(0)).intValue(),v=((Number)e.get(1)).intValue();adj[u][degree[u]++]=v;adj[v][degree[v]++]=u;}
        int[] parent=new int[n],order=new int[n];Arrays.fill(parent,-2);
        int count=1;order[0]=0;parent[0]=-1;
        for(int at=0;at<count;at++){
            int u=order[at];
            for(int v:adj[u])if(v!=parent[u]){parent[v]=u;order[count++]=v;}
        }
        long[] take=new long[n],skip=new long[n];
        for(int at=n-1;at>=0;at--){
            int u=order[at];take[u]=((Number)weights.get(u)).longValue();
            for(int v:adj[u])if(parent[v]==u){take[u]+=skip[v];skip[u]+=Math.max(take[v],skip[v]);}
        }
        List<Integer> selected=new ArrayList<>();
        boolean[] chosen=new boolean[n];
        for(int at=0;at<n;at++){
            int u=order[at];
            chosen[u]=(parent[u]<0||!chosen[parent[u]])&&take[u]>skip[u];
            if(chosen[u])selected.add(u);
        }
        return Map.of("weight",Math.max(take[0],skip[0]),"selected",selected);
    }

    private static List<Integer> emptyMatches(int n){
        List<Integer> out=new ArrayList<>(n+1);
        for(int i=0;i<=n;i++)out.add(i);
        return out;
    }
    static Map<String,Object> naive(Map<String,Object> input){
        String text=(String)input.get("text"),pattern=(String)input.get("pattern");
        int n=text.length(),m=pattern.length();
        if(m==0)return Map.of("positions",emptyMatches(n));
        List<Integer> out=new ArrayList<>();
        for(int i=0;i+m<=n;i++){
            int j=0;while(j<m&&text.charAt(i+j)==pattern.charAt(j))j++;
            if(j==m)out.add(i);
        }
        return Map.of("positions",out);
    }
    static Map<String,Object> kmp(Map<String,Object> input){
        String text=(String)input.get("text"),pattern=(String)input.get("pattern");
        int n=text.length(),m=pattern.length();
        if(m==0)return Map.of("positions",emptyMatches(n));
        int[] pi=new int[m];
        for(int i=1,j=0;i<m;i++){
            while(j>0&&pattern.charAt(i)!=pattern.charAt(j))j=pi[j-1];
            if(pattern.charAt(i)==pattern.charAt(j))j++;
            pi[i]=j;
        }
        List<Integer> out=new ArrayList<>();
        for(int i=0,j=0;i<n;i++){
            while(j>0&&text.charAt(i)!=pattern.charAt(j))j=pi[j-1];
            if(text.charAt(i)==pattern.charAt(j))j++;
            if(j==m){out.add(i-m+1);j=pi[j-1];}
        }
        return Map.of("positions",out);
    }
    static Map<String,Object> rk(Map<String,Object> input){
        String text=(String)input.get("text"),pattern=(String)input.get("pattern");
        int n=text.length(),m=pattern.length();
        if(m==0)return Map.of("positions",emptyMatches(n));
        List<Integer> out=new ArrayList<>();
        if(m>n)return Map.of("positions",out);
        final long base=31,mod=101;
        long high=1,ph=0,th=0;
        for(int i=1;i<m;i++)high=(high*base)%mod;
        for(int i=0;i<m;i++){
            ph=(ph*base+pattern.charAt(i))%mod;
            th=(th*base+text.charAt(i))%mod;
        }
        for(int i=0;i+m<=n;i++){
            if(ph==th){
                int j=0;while(j<m&&text.charAt(i+j)==pattern.charAt(j))j++;
                if(j==m)out.add(i);
            }
            if(i+m<n){
                th=(th-text.charAt(i)*high%mod+mod)%mod;
                th=(th*base+text.charAt(i+m))%mod;
            }
        }
        return Map.of("positions",out);
    }

    static Map<String,Object> suffix(Map<String,Object> input){
        String s=(String)input.get("text");int n=s.length();
        if(n==0)return Map.of("sa",List.of(),"lcp",List.of(),"repeated","");
        int[] sa=new int[n],rank=new int[n],next=new int[n],temp=new int[n];
        for(int i=0;i<n;i++){sa[i]=i;rank[i]=s.charAt(i)-31;}
        int classes=95;
        for(int step=1;step<n;step*=2){
            radixSort(sa,temp,rank,step,classes);
            next[sa[0]]=1;int newClasses=1;
            for(int i=1;i<n;i++){
                int a=sa[i-1],b=sa[i];
                if(rank[a]!=rank[b]||(a+step<n?rank[a+step]:0)!=(b+step<n?rank[b+step]:0))newClasses++;
                next[b]=newClasses;
            }
            int[] swap=rank;rank=next;next=swap;classes=newClasses;
            if(classes==n)break;
        }
        int[] inv=new int[n],lcp=new int[n];
        for(int i=0;i<n;i++)inv[sa[i]]=i;
        int h=0,best=0,start=0;
        for(int i=0;i<n;i++){
            int pos=inv[i];
            if(pos==0){h=0;continue;}
            int j=sa[pos-1];
            while(i+h<n&&j+h<n&&s.charAt(i+h)==s.charAt(j+h))h++;
            lcp[pos]=h;
            if(h>best){best=h;start=i;}
            if(h>0)h--;
        }
        List<Integer> saOut=new ArrayList<>(n),lcpOut=new ArrayList<>(n);
        for(int i=0;i<n;i++){saOut.add(sa[i]);lcpOut.add(lcp[i]);}
        return Map.of("sa",saOut,"lcp",lcpOut,"repeated",s.substring(start,start+best));
    }
    private static void radixSort(int[] sa,int[] temp,int[] rank,int step,int classes){
        int n=sa.length;int[] count=new int[classes+1];
        for(int x:sa)count[x+step<n?rank[x+step]:0]++;
        int sum=0;for(int i=0;i<count.length;i++){int c=count[i];count[i]=sum;sum+=c;}
        for(int x:sa)temp[count[x+step<n?rank[x+step]:0]++]=x;
        Arrays.fill(count,0);
        for(int x:temp)count[rank[x]]++;
        sum=0;for(int i=0;i<count.length;i++){int c=count[i];count[i]=sum;sum+=c;}
        for(int x:temp)sa[count[rank[x]]++]=x;
    }

    static Map<String,Object> aho(Map<String,Object> input){
        List<?> patterns=(List<?>)input.get("patterns"),texts=(List<?>)input.get("texts");
        int p=patterns.size();
        List<int[]> next=new ArrayList<>();
        next.add(new int[95]);
        int[] terminal=new int[p];
        for(int i=0;i<p;i++){
            String pat=(String)patterns.get(i);int v=0;
            for(int j=0;j<pat.length();j++){
                int c=pat.charAt(j)-32;
                int u=next.get(v)[c];
                if(u==0){u=next.size();next.get(v)[c]=u;next.add(new int[95]);}
                v=u;
            }
            terminal[i]=v;
        }
        int states=next.size();int[] fail=new int[states],bfs=new int[states];
        int head=0,tail=0;
        for(int c=0;c<95;c++){
            int v=next.get(0)[c];
            if(v!=0)bfs[tail++]=v;
        }
        while(head<tail){
            int u=bfs[head++];int[] row=next.get(u),frow=next.get(fail[u]);
            for(int c=0;c<95;c++){
                int v=row[c];
                if(v!=0){fail[v]=frow[c];bfs[tail++]=v;}
                else row[c]=frow[c];
            }
        }
        List<List<Long>> results=new ArrayList<>();
        for(Object obj:texts){
            String s=(String)obj;long[] visits=new long[states];int v=0;
            for(int i=0;i<s.length();i++){
                v=next.get(v)[s.charAt(i)-32];visits[v]++;
            }
            for(int i=tail-1;i>=0;i--){int u=bfs[i];visits[fail[u]]+=visits[u];}
            List<Long> row=new ArrayList<>(p);
            for(int i=0;i<p;i++)row.add(visits[terminal[i]]);
            results.add(row);
        }
        return Map.of("counts",results);
    }
}
