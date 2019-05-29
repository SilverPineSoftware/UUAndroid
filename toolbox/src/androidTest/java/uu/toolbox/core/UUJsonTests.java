package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.espresso.core.internal.deps.guava.base.Objects;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Iterator;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUJsonTests
{
    private static void assertEquals(@Nullable final BaseballPlayer lhs, @Nullable final BaseballPlayer rhs)
    {
        Assert.assertNotNull(lhs);
        Assert.assertNotNull(rhs);
        Assert.assertEquals(lhs.position, rhs.position);
        Assert.assertEquals(lhs.number, rhs.number);
        Assert.assertEquals(lhs.name, rhs.name);
        Assert.assertEquals(lhs.birthday, rhs.birthday);
        Assert.assertEquals(lhs.average, rhs.average);
        Assert.assertEquals(lhs.isActive, rhs.isActive);
    }

    private static void assertEquals(@Nullable final JSONObject lhs, @Nullable final JSONObject rhs)
    {
        if (lhs == null && rhs != null)
        {
            Assert.fail("LHS UserInfo null and RHS UserInfo non null");
        }

        if (lhs != null && rhs == null)
        {
            Assert.fail("RHS UserInfo null and LHS UserInfo non null");
        }

        if (lhs != null && rhs != null)
        {
            Assert.assertEquals(lhs.length(), rhs.length());

            Iterator<String> lhsKeys = lhs.keys();
            Iterator<String> rhsKeys = lhs.keys();
            while (lhsKeys.hasNext())
            {
                String lhsKey = lhsKeys.next();
                Object lhsVal = lhs.opt(lhsKey);

                boolean keyFound = false;
                boolean valEqual = false;

                while (rhsKeys.hasNext())
                {
                    String rhsKey = rhsKeys.next();
                    Object rhsVal = rhs.opt(rhsKey);

                    if (lhsKey.equals(rhsKey))
                    {
                        keyFound = true;

                        if (lhsVal instanceof JSONObject && rhsVal instanceof JSONObject)
                        {
                            assertEquals((JSONObject)lhsVal, (JSONObject)rhsVal);
                            valEqual = true; // If not it will assert recursively
                        }
                        else
                        {
                            valEqual = Objects.equal(lhsVal, rhsVal);
                        }

                        break;
                    }
                }

                Assert.assertTrue(keyFound);
                Assert.assertTrue(valEqual);
            }
        }
    }

    private static void assertEquals(@Nullable final UUError lhs, @Nullable final UUError rhs)
    {
        Assert.assertNotNull(lhs);
        Assert.assertNotNull(rhs);
        Assert.assertEquals(lhs.getCode(), rhs.getCode());
        Assert.assertEquals(lhs.getDomain(), rhs.getDomain());

        Exception exLhs = lhs.getException();
        Exception exRhs = lhs.getException();
        if (exLhs == null && exRhs != null)
        {
            Assert.fail("LHS Exception null and RHS Exception non null");
        }

        if (exLhs != null && exRhs == null)
        {
            Assert.fail("RHS Exception null and LHS Exception non null");
        }

        if (exLhs != null && exRhs != null)
        {
            Assert.assertEquals(exLhs.getMessage(), exRhs.getMessage());
            Assert.assertEquals(exLhs.getLocalizedMessage(), exRhs.getLocalizedMessage());
            Assert.assertEquals(exLhs.getClass(), exRhs.getClass());
        }

        assertEquals(lhs.getUserInfo(), rhs.getUserInfo());
    }

    private static BaseballPlayer randomPlayer()
    {
        BaseballPlayer p = new BaseballPlayer();
        p.position = Position.CenterField;
        p.number = 15;
        p.name = "Code Monkey";
        p.birthday = System.currentTimeMillis();
        p.average = 0.357f;
        p.isActive = true;
        return p;
    }

    @Test
    public void test_0000_testToFromJson() throws Exception
    {
        BaseballPlayer p = randomPlayer();
        JSONObject obj = p.toJsonObject();

        BaseballPlayer deserialized = UUJson.parseJsonObject(BaseballPlayer.class, obj);
        assertEquals(p, deserialized);
    }

    @Test
    public void test_0001_testUUError_CodeDomain() throws Exception
    {
        UUError input = new UUError("TEST", 57);
        JSONObject obj = input.toJsonObject();
        System.out.println(obj.toString());

        UUError deserialized = UUJson.parseJsonObject(UUError.class, obj);
        assertEquals(input, deserialized);
    }

    @Test
    public void test_0002_testUUError_CodeDomainException() throws Exception
    {
        UUError input = new UUError("TEST", 0xDEADBEEF);
        input.setException(new IllegalArgumentException("foo"));
        JSONObject obj = input.toJsonObject();
        System.out.println(obj.toString());

        UUError deserialized = UUJson.parseJsonObject(UUError.class, obj);
        assertEquals(input, deserialized);
    }

    @Test
    public void test_0003_testUUError_CodeDomainUserInfo() throws Exception
    {
        UUError input = new UUError("TEST", 0xDEADBEEF);
        input.addUserInfo(57, 99);
        input.addUserInfo("Name", "Bob");
        JSONObject obj = input.toJsonObject();
        System.out.println(obj.toString());

        UUError deserialized = UUJson.parseJsonObject(UUError.class, obj);
        assertEquals(input, deserialized);
    }

    @Test
    public void test_0004_testUUError_CodeDomainUserInfoInner() throws Exception
    {
        UUError input = new UUError("TEST", 0xDEADBEEF);
        input.addUserInfo(57, 99);
        input.addUserInfo("Name", "Bob");

        JSONObject inner = new JSONObject();
        inner.put("Testing", "1234");
        inner.put("Hello", "World");
        input.addUserInfo("Extra", inner);

        JSONObject obj = input.toJsonObject();
        System.out.println(obj.toString());

        UUError deserialized = UUJson.parseJsonObject(UUError.class, obj);
        assertEquals(input, deserialized);
    }
}

enum Position
{
    Pitcher,
    Catcher,
    FirstBase,
    SecondBase,
    ThirdBase,
    ShortStop,
    LeftField,
    CenterField,
    RightField
}

class BaseballPlayer implements UUJsonConvertible
{
    private enum JsonKeys
    {
        Position,
        Number,
        Name,
        Birthday,
        Average,
        IsActive
    }

    Position position;
    int number;
    String name;
    long birthday;
    double average;
    boolean isActive;

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();

        UUJson.safePut(json, JsonKeys.Position, position);
        UUJson.safePut(json, JsonKeys.Number, number);
        UUJson.safePut(json, JsonKeys.Name, name);
        UUJson.safePut(json, JsonKeys.Birthday, birthday);
        UUJson.safePut(json, JsonKeys.Average, average);
        UUJson.safePut(json, JsonKeys.IsActive, isActive);


        return json;
    }

    @Override
    public void fillFromJson(@NonNull JSONObject json)
    {
        position = UUJson.safeGetEnum(Position.class, json, JsonKeys.Position);
        number = UUJson.safeGetInt(json, JsonKeys.Number);
        name = UUJson.safeGetString(json, JsonKeys.Name);
        birthday = UUJson.safeGetLong(json, JsonKeys.Birthday);
        average = UUJson.safeGetDouble(json, JsonKeys.Average);
        isActive = UUJson.safeGetBool(json, JsonKeys.IsActive);
    }
}

