package steve6472.funnylib.item.builtin;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * @author <a href="http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html">Andreas Kahler</a>
 */
public class IcoSphereCreator
{
    public record TriangleIndices(int v1, int v2, int v3) { }

    public static class MeshGeometry3D
    {
        public List<Vector3f> positions = new ArrayList<>();
        public List<Integer> triangleIndices = new ArrayList<>();
    }

    private MeshGeometry3D geometry;
    private int index;
    private Dictionary<Long, Integer> middlePointIndexCache;

    // add vertex to mesh, fix position to be on unit sphere, return index
    private int addVertex(Vector3f p)
    {
        float length = p.length();
        geometry.positions.add(new Vector3f(p.x / length, p.y / length, p.z / length));
        return index++;
    }

    // return index of point in the middle of p1 and p2
    private int getMiddlePoint(int p1, int p2)
    {
        // first check if we have it already
        boolean firstIsSmaller = p1 < p2;
        long smallerIndex = firstIsSmaller ? p1 : p2;
        long greaterIndex = firstIsSmaller ? p2 : p1;
        long key = (smallerIndex << 32) + greaterIndex;

        Integer ret = this.middlePointIndexCache.get(key);
        if (ret != null) {
            return ret;
        }

        // not in cache, calculate it
        Vector3f point1 = this.geometry.positions.get(p1);
        Vector3f point2 = this.geometry.positions.get(p2);
        Vector3f middle = new Vector3f(
            (point1.x + point2.x) / 2.0f,
            (point1.y + point2.y) / 2.0f,
            (point1.z + point2.z) / 2.0f);

        // add vertex makes sure point is on unit sphere
        int i = addVertex(middle);

        // store it, return index
        this.middlePointIndexCache.put(key, i);
        return i;
    }

    public MeshGeometry3D create(int recursionLevel)
    {
        this.geometry = new MeshGeometry3D();
        this.middlePointIndexCache = new Hashtable<>();
        this.index = 0;

        // create 12 vertices of a icosahedron
        float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0f);

        addVertex(new Vector3f(-1,  t,  0));
        addVertex(new Vector3f( 1,  t,  0));
        addVertex(new Vector3f(-1, -t,  0));
        addVertex(new Vector3f( 1, -t,  0));

        addVertex(new Vector3f( 0, -1,  t));
        addVertex(new Vector3f( 0,  1,  t));
        addVertex(new Vector3f( 0, -1, -t));
        addVertex(new Vector3f( 0,  1, -t));

        addVertex(new Vector3f( t,  0, -1));
        addVertex(new Vector3f( t,  0,  1));
        addVertex(new Vector3f(-t,  0, -1));
        addVertex(new Vector3f(-t,  0,  1));


        // create 20 triangles of the icosahedron
        var faces = new ArrayList<TriangleIndices>();

        // 5 faces around point 0
        faces.add(new TriangleIndices(0, 11, 5));
        faces.add(new TriangleIndices(0, 5, 1));
        faces.add(new TriangleIndices(0, 1, 7));
        faces.add(new TriangleIndices(0, 7, 10));
        faces.add(new TriangleIndices(0, 10, 11));

        // 5 adjacent faces
        faces.add(new TriangleIndices(1, 5, 9));
        faces.add(new TriangleIndices(5, 11, 4));
        faces.add(new TriangleIndices(11, 10, 2));
        faces.add(new TriangleIndices(10, 7, 6));
        faces.add(new TriangleIndices(7, 1, 8));

        // 5 faces around point 3
        faces.add(new TriangleIndices(3, 9, 4));
        faces.add(new TriangleIndices(3, 4, 2));
        faces.add(new TriangleIndices(3, 2, 6));
        faces.add(new TriangleIndices(3, 6, 8));
        faces.add(new TriangleIndices(3, 8, 9));

        // 5 adjacent faces
        faces.add(new TriangleIndices(4, 9, 5));
        faces.add(new TriangleIndices(2, 4, 11));
        faces.add(new TriangleIndices(6, 2, 10));
        faces.add(new TriangleIndices(8, 6, 7));
        faces.add(new TriangleIndices(9, 8, 1));


        // refine triangles
        for (int i = 0; i < recursionLevel; i++)
        {
            var faces2 = new ArrayList<TriangleIndices>();
            for (TriangleIndices tri : faces)
            {
                // replace triangle by 4 triangles
                int a = getMiddlePoint(tri.v1, tri.v2);
                int b = getMiddlePoint(tri.v2, tri.v3);
                int c = getMiddlePoint(tri.v3, tri.v1);

                faces2.add(new TriangleIndices(tri.v1, a, c));
                faces2.add(new TriangleIndices(tri.v2, b, a));
                faces2.add(new TriangleIndices(tri.v3, c, b));
                faces2.add(new TriangleIndices(a, b, c));
            }
            faces = faces2;
        }

        // done, now add triangles to mesh
        for (TriangleIndices tri : faces)
        {
            this.geometry.triangleIndices.add(tri.v1);
            this.geometry.triangleIndices.add(tri.v2);
            this.geometry.triangleIndices.add(tri.v3);
        }

        return this.geometry;
    }
}