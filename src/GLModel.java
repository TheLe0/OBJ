import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.media.opengl.*;

public class GLModel{

    private ArrayList vertexsets;
    private ArrayList vertexsetsnorms;
    private ArrayList vertexsetstexs;
    private ArrayList faces;
    private ArrayList facestexs;
    private ArrayList facesnorms;
    private int objectlist;
    private int numpolys;
    public float toppoint;
    public float bottompoint;
    public float leftpoint;
    public float rightpoint;
    public float farpoint;
    public float nearpoint;

    //CARREGA O MODEL
    public GLModel(BufferedReader ref, boolean centerit, GL2 gl){
        
        vertexsets = new ArrayList();
        vertexsetsnorms = new ArrayList();
        vertexsetstexs = new ArrayList();
        faces = new ArrayList();
        facestexs = new ArrayList();
        facesnorms = new ArrayList();
        numpolys = 0;
        toppoint = 0.0F;
        bottompoint = 0.0F;
        leftpoint = 0.0F;
        rightpoint = 0.0F;
        farpoint = 0.0F;
        nearpoint = 0.0F;
        loadobject(ref);
        if(centerit)
            centerit();
        opengldrawtolist(gl);
        numpolys = faces.size();
        cleanup();
    }

    private void cleanup(){
        vertexsets.clear();
        vertexsetsnorms.clear();
        vertexsetstexs.clear();
        faces.clear();
        facestexs.clear();
        facesnorms.clear();
    }

    private void loadobject(BufferedReader br){
        int linecounter = 0;
        int facecounter = 0;
        try{
            boolean firstpass = true;
            String newline;
            while((newline = br.readLine()) != null){
                linecounter++;
                if(newline.length() > 0){
                    newline = newline.trim();
                    
                    //LOADS VERTEX COORDINATES
                    if(newline.startsWith("v ")){
                        float coords[] = new float[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(2, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for(int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Float.parseFloat(st.nextToken());

                        if(firstpass){
                            rightpoint = coords[0];
                            leftpoint = coords[0];
                            toppoint = coords[1];
                            bottompoint = coords[1];
                            nearpoint = coords[2];
                            farpoint = coords[2];
                            firstpass = false;
                        }
                        if(coords[0] > rightpoint)
                            rightpoint = coords[0];
                        if(coords[0] < leftpoint)
                            leftpoint = coords[0];
                        if(coords[1] > toppoint)
                            toppoint = coords[1];
                        if(coords[1] < bottompoint)
                            bottompoint = coords[1];
                        if(coords[2] > nearpoint)
                            nearpoint = coords[2];
                        if(coords[2] < farpoint)
                            farpoint = coords[2];
                        vertexsets.add(coords);
                    }
                    else
                    
                    //LOADS VERTEX TEXTURE COORDINATES
                    if(newline.startsWith("vt")){
                        float coords[] = new float[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(3, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for(int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Float.parseFloat(st.nextToken());

                        vertexsetstexs.add(coords);
                    }
                    else
                    
                    //LOADS VERTEX NORMALS COORDINATES
                    if(newline.startsWith("vn")){
                        float coords[] = new float[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(3, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for(int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Float.parseFloat(st.nextToken());

                        vertexsetsnorms.add(coords);
                    }
                    else
                    
                    //LOADS FACES COORDINATES
                    if(newline.startsWith("f ")){
                        facecounter++;
                        newline = newline.substring(2, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        int count = st.countTokens();
                        int v[] = new int[count];
                        int vt[] = new int[count];
                        int vn[] = new int[count];
                        for(int i = 0; i < count; i++){
                            char chars[] = st.nextToken().toCharArray();
                            StringBuffer sb = new StringBuffer();
                            char lc = 'x';
                            for(int k = 0; k < chars.length; k++){
                                if(chars[k] == '/' && lc == '/')
                                    sb.append('0');
                                lc = chars[k];
                                sb.append(lc);
                            }

                            StringTokenizer st2 = new StringTokenizer
                            (sb.toString(), "/");
                            int num = st2.countTokens();
                            v[i] = Integer.parseInt(st2.nextToken());
                            if(num > 1)
                                vt[i] = Integer.parseInt(st2.nextToken());
                            else
                                vt[i] = 0;
                            if(num > 2)
                                vn[i] = Integer.parseInt(st2.nextToken());
                            else
                                vn[i] = 0;
                        }

                        faces.add(v);
                        facestexs.add(vt);
                        facesnorms.add(vn);
                    }
                }
             }
        }
        catch(IOException e){
            System.out.println("Failed to read file: " + br.toString());
        }
        catch(NumberFormatException e){
            System.out.println("Malformed OBJ file: " + br.toString() + "\r \r"+ e.getMessage());
        }
    }
    
    private void centerit(){
        float xshift = (rightpoint - leftpoint) / 2.0F;
        float yshift = (toppoint - bottompoint) / 2.0F;
        float zshift = (nearpoint - farpoint) / 2.0F;
        for(int i = 0; i < vertexsets.size(); i++){
            float coords[] = new float[4];
            coords[0] = ((float[])vertexsets.get(i))[0] - leftpoint - xshift;
            coords[1] = ((float[])vertexsets.get(i))[1] - bottompoint - yshift;
            coords[2] = ((float[])vertexsets.get(i))[2] - farpoint - zshift;
            vertexsets.set(i, coords);
        }

    }

    public float getXWidth(){
        float returnval = 0.0F;
        returnval = rightpoint - leftpoint;
        return returnval;
    }

    public float getYHeight(){
        float returnval = 0.0F;
        returnval = toppoint - bottompoint;
        return returnval;
    }

    public float getZDepth(){
        float returnval = 0.0F;
        returnval = nearpoint - farpoint;
        return returnval;
    }

    public int numpolygons(){
        return numpolys;
    }

    public void opengldrawtolist(GL2 gl){

        this.objectlist = gl.glGenLists(1);
                
        gl.glNewList(objectlist,GL2.GL_COMPILE);
        for (int i=0;i<faces.size();i++) {
            
            int[] tempfaces = (int[])(faces.get(i));
            int[] tempfacesnorms = (int[])(facesnorms.get(i));
            int[] tempfacestexs = (int[])(facestexs.get(i));
            
            //// Quad Begin Header ////
            int polytype;
            if (tempfaces.length == 3) {
                polytype = gl.GL_TRIANGLES;
            } else if (tempfaces.length == 4) {
                polytype = gl.GL_QUADS;
            } else {
                polytype = gl.GL_POLYGON;
            }
            gl.glBegin(polytype);
            
            for (int w=0;w<tempfaces.length;w++) {
                if (tempfacesnorms[w] != 0) {
                    float normtempx = ((float[])vertexsetsnorms.get(tempfacesnorms[w] - 1))[0];
                    float normtempy = ((float[])vertexsetsnorms.get(tempfacesnorms[w] - 1))[1];
                    float normtempz = ((float[])vertexsetsnorms.get(tempfacesnorms[w] - 1))[2];
                    gl.glNormal3f(normtempx, normtempy, normtempz);
                }
                
                if (tempfacestexs[w] != 0) {
                    float textempx = ((float[])vertexsetstexs.get(tempfacestexs[w] - 1))[0];
                    float textempy = ((float[])vertexsetstexs.get(tempfacestexs[w] - 1))[1];
                    float textempz = ((float[])vertexsetstexs.get(tempfacestexs[w] - 1))[2];
                    gl.glTexCoord3f(textempx,1f-textempy,textempz);
                }
                
                float tempx = ((float[])vertexsets.get(tempfaces[w] - 1))[0];
                float tempy = ((float[])vertexsets.get(tempfaces[w] - 1))[1];
                float tempz = ((float[])vertexsets.get(tempfaces[w] - 1))[2];
                gl.glVertex3f(tempx,tempy,tempz);
            }
            
            gl.glEnd();
            
        }
        gl.glEndList();
    }
    
    public void opengldraw(GL2 gl){
        gl.glCallList(objectlist);
    }
}
