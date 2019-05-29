package uu.toolbox.data;

@UUSqlTable(tableName = "uu_object_primitives", existsInVersion = 4)
public class UUDataModelWithObjPrimitiveTypes implements UUDataModel
{
    @UUSqlColumn(name = "MyInt", type = UUSqlColumn.Type.INT_32)
    public Integer anInt;

    @UUSqlColumn(name = "YourFloat", type = UUSqlColumn.Type.REAL)
    public Float aFloat;

    @UUSqlColumn(name = "TheirFloat", type = UUSqlColumn.Type.REAL)
    public float aPrimFloat;
}
