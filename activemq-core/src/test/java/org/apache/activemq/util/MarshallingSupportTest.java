begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @author rajdavies  */
end_comment

begin_class
specifier|public
class|class
name|MarshallingSupportTest
extends|extends
name|TestCase
block|{
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test method for      * {@link org.apache.activemq.util.MarshallingSupport#propertiesToString(java.util.Properties)}.      *       * @throws Exception      */
specifier|public
name|void
name|testPropertiesToString
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"key"
operator|+
name|i
decl_stmt|;
name|String
name|value
init|=
literal|"value"
operator|+
name|i
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
name|MarshallingSupport
operator|.
name|propertiesToString
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|Properties
name|props2
init|=
name|MarshallingSupport
operator|.
name|stringToProperties
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|props
argument_list|,
name|props2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

