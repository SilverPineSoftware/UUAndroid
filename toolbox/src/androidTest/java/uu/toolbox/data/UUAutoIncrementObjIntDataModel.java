package uu.toolbox.data;

import org.junit.Assert;

import uu.toolbox.core.UURandom;

@UUSqlTable(existsInVersion = 4)
public class UUAutoIncrementObjIntDataModel implements UUDataModel
{
    @UUSqlColumn(type = UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT)
    public Integer id;

    @UUSqlColumn()
    public String name;

    @UUSqlColumn()
    public int score;

    @UUSqlColumn()
    public long timestamp;

    public static UUAutoIncrementObjIntDataModel random()
    {
        UUAutoIncrementObjIntDataModel m = new UUAutoIncrementObjIntDataModel();
        m.score = UURandom.randomInt();
        m.timestamp = System.currentTimeMillis();
        m.name = UURandom.randomString(20);
        m.id = null;
        return m;
    }

    public static void assertEquals(UUAutoIncrementObjIntDataModel obj, UUAutoIncrementObjIntDataModel other)
    {
        Assert.assertNotNull(obj);
        Assert.assertNotNull(other);
        Assert.assertEquals(obj.id, other.id);
        Assert.assertEquals(obj.name, other.name);
        Assert.assertEquals(obj.score, other.score);
        Assert.assertEquals(obj.timestamp, other.timestamp);
    }

}
