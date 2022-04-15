import java.io.*;
import java.util.Scanner;


public class BufferPool {
    private Frame[] buffers;
    private int removeIndex;
    private int numRecPerBlock=100;

//    public void initialize(int sizeOfArray){}

    public BufferPool(int sizeOfArray){
        this.buffers = new Frame[sizeOfArray];
        for(int i = 0; i < sizeOfArray; i++){
            buffers[i] = new Frame();
        }
        this.removeIndex = 0;
        this.numRecPerBlock = 100;
    }

    private int existsInPool(int blkID){
        for(int i = 0; i < this.buffers.length; i++){
            if(buffers[i].getBlkID() == blkID){
                return i;
            }
        }
        return -1;
    }


    private int getBlockFromDisk(int blkID){
        int emptyFrame = getEmptyFrame();
        if(emptyFrame == -1){
            emptyFrame = findsRmvblFrame();
            if(emptyFrame == -1){
                return emptyFrame;
            }
        }

        if(this.buffers[emptyFrame].isDirty()){
            writeToDisk(this.buffers[emptyFrame].getContent(), this.buffers[emptyFrame].getBlkID());
        }

        readBlock(emptyFrame, blkID);
        return emptyFrame;
    }

    private int getEmptyFrame(){
        for(int j = 0; j < this.buffers.length; j++){
            if(buffers[j].getBlkID() == -1){
                return j;
            }
        }
        return -1;
    }
    private void writeToDisk(byte[] content, int blkID){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Project1/F" + blkID + ".txt"));
            writer.write(String.valueOf(content));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // read contents of file from a block in disk
    private void readBlock(int emptyFrame, int blkID){
        Scanner scanner = null;
        try{
            scanner = new Scanner(new File("Project1/F" + blkID + ".txt")).useDelimiter("\\Z");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        StringBuilder content = new StringBuilder(4000);
        while(scanner.hasNext()){
            content.append(scanner.next());
        }
        scanner.close();
        this.buffers[emptyFrame].setContent(content.toString().getBytes());
        this.buffers[emptyFrame].setBlockID(blkID);
        this.buffers[emptyFrame].setDirty(false);
    }

    private byte[] getRecFromPool(int frameNum, int recNum){
        return this.buffers[frameNum].getSpecificRecord(recNum);
    }


    private int findsRmvblFrame(){
        for(int i = this.removeIndex; i < this.buffers.length; i++){
            if(!this.buffers[i].isPinned()){
                removeIndex = i + 1;
                System.out.println("Evicted File " + this.buffers[i].getBlkID() + " from Frame " + (i + 1));
                return i;
            }
        }

        for(int i = 0; i < removeIndex; i++){
            if(!this.buffers[i].isPinned()){
                removeIndex = i + 1;
                System.out.println("Removed file " + this.buffers[i].getBlkID() + " from Frame " + (i + 1));
                return i;
            }
        }
        return -1;
    }

    public void GET(int currentRecordID){
        int blkID = ((currentRecordID - 1)/this.numRecPerBlock) + 1;
        int blkFrame = existsInPool(blkID);
        if(blkFrame != -1){
            System.out.println(getRecFromPool(blkFrame, (currentRecordID % 100) - 1));
            System.out.println("File " + blkID + " is already in memory");
            System.out.println("File is in Frame" + (blkFrame + 1));
        } else {
            blkFrame = getBlockFromDisk(blkID);
            if(blkFrame != -1) {
                System.out.println(getRecFromPool(blkFrame, (currentRecordID % 100) - 1));
                System.out.println("File " + blkID + "is taken from disk");
                System.out.println("File is in Frame " + (blkFrame + 1));
            } else {
                System.out.println("Block" + blkID + " cant be accessed");
            }

        }

    }

    public void SET(int currentRecordID, byte[] record){
        int blkID = ((currentRecordID - 1)/this.numRecPerBlock) + 1;
        int blkFrame = existsInPool(blkID);
        if(blkFrame != -1){
            this.buffers[blkFrame].setSpecificRecord((currentRecordID % this.numRecPerBlock) - 1, record);
            System.out.println("File" + blkID + " is already in memory");
            System.out.println("File is in Frame" + (blkFrame + 1));
        } else {
            blkFrame = getBlockFromDisk(blkID);
            if(blkFrame == -1){
                System.out.println("Block" + blkID + " cant be accessed. Didnt write a record in block");
            } else {
                this.buffers[blkFrame].setSpecificRecord((currentRecordID % this.numRecPerBlock) - 1, record);
                System.out.println("File" + blkID + "is taken from disk");
                System.out.println("File is in Frame" + (blkFrame + 1));
            }
        }
    }

    public void PIN(int blkID){
        int blkFrame = existsInPool(blkID);
        if(blkFrame != -1){
            //if(blkFrame != -1)blkFrame = getBlockFromDisk(blkID);
            System.out.println("file" + blkID + "is pinned in Frame:" + (blkFrame + 1));
            if(this.buffers[blkFrame].isPinned()){
                System.out.println("is already pinned");
            } else {


                this.buffers[blkFrame].setPinned(true);
                System.out.println("Frame " + (blkFrame + 1) + " was pinned");
            }
        } else {
            blkFrame = getBlockFromDisk(blkID);
//            bgetBlockFromDisk(blkID+1);

            if(blkFrame != -1) {
                this.buffers[blkFrame].setPinned(true);
                System.out.println("File" + blkID + "is already pinned in frame"+ (blkFrame + 1));
                System.out.println("Frame" + (blkFrame + 1) + "was pinned");
            } else {
                System.out.println("Block" + blkID + "cannot be pinned" );
            }
        }
    }

    public void UNPIN(int blkID){
        int blkFrame = existsInPool(blkID);
        if(blkFrame != -1){
            //if(blkFrame != -1)blkFrame = getBlockFromDisk(blkID);

            System.out.println("file" + blkID + "is pinned in Frame:" + (blkFrame + 1));
            if(this.buffers[blkFrame].isPinned()){
                this.buffers[blkFrame].setPinned(false);
                System.out.println("Frame" + (blkFrame + 1) + "was unpinned");
            } else {
                System.out.println("Frame was unpinned");
            }
        } else {
            System.out.println("Block " + blkID + " cannot be unpinned");
        }
    }




}
