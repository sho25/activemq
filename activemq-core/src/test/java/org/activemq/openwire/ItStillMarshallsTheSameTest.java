begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|openwire
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|ItStillMarshallsTheSameTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testAll
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerInfoData
operator|.
name|assertAllControlFileAreEqual
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

