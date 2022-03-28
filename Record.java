public class Record {
    public static final int EMPTY = -1, INUSE = 1;
    private int blkID;
    private int recID;

    public Record(int blkID,int recID){
        this.recID=recID;
        this.blkID=blkID;
    }
}
