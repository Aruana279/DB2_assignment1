public class Frame {
    private byte[] content=new byte[4*1024];
    private boolean dirty;
    private boolean pinned;
    private int blkID;
    private int recordSize = 40;



//    public boolean equals(Object obj) {
//        RID r = (RID) obj;
//        return blknum == r.blknum && id==r.id;
//    }

//    public String toString() {
//        return "[" + blknum + ", " + id + "]";
//    }
//    public void setContent(byte[] content) {
//        this.content = content;
//    }



    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setBlockID(int blockID) {
        this.blkID = blockID;
    }



    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content){this.content = content;}

    public boolean isDirty() {
        return dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public int getBlkID() {
        return blkID;
    }

    public byte[] getSpecificRecord(int numOfRecord){
        byte[] record = new byte[this.recordSize];
        for(int i = 0; i < this.recordSize; i++){
            record[i] = this.content[i + (this.recordSize * numOfRecord)];
        }
        return record;
    }

    public void setSpecificRecord(int numOfRecord, byte[] contentOfSpecRecord){
        this.dirty = true;
        for(int i = 0; i < this.recordSize; i++){
            content[i + (this.recordSize * numOfRecord)] = contentOfSpecRecord[i];
        }
    }

    public void makeDirty(int numOfRecord, byte[] record){
        for(int i = 0; i < this.recordSize; i++){
            content[i + (this.recordSize * numOfRecord)] = record[i];
        }
        this.dirty = true;
    }





}
