begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|leveldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|LevelDBStoreBrokerTest
extends|extends
name|BrokerTest
block|{
comment|//    def suite: Test = {
comment|//      return new TestSuite(classOf[LevelDBStoreBrokerTest])
comment|//    }
comment|//
comment|//    def main(args: Array[String]): Unit = {
comment|//      junit.textui.TestRunner.run(suite)
comment|//    }
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|createPersistenceAdapter
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|PersistenceAdapter
name|createPersistenceAdapter
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
name|LevelDBStore
name|store
init|=
operator|new
name|LevelDBStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/activemq-data/leveldb"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|store
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
block|}
return|return
name|store
return|;
block|}
specifier|protected
name|BrokerService
name|createRestartedBroker
parameter_list|()
throws|throws
name|IOException
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|createPersistenceAdapter
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

