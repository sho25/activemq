begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|bugs
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
name|ActiveMQConnectionFactory
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
name|ActiveMQXAConnection
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
name|ActiveMQXAConnectionFactory
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
name|BrokerFactory
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
name|jmx
operator|.
name|BrokerMBeanSupport
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
name|jmx
operator|.
name|BrokerViewMBean
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
name|jmx
operator|.
name|DestinationViewMBean
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
name|jmx
operator|.
name|PersistenceAdapterViewMBean
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
name|jmx
operator|.
name|QueueViewMBean
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
name|jmx
operator|.
name|RecoveredXATransactionViewMBean
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
name|command
operator|.
name|TransactionId
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
name|command
operator|.
name|XATransactionId
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
name|util
operator|.
name|JMXSupport
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|FileNotFoundException
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|AMQ7067Test
block|{
specifier|protected
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
specifier|static
name|String
name|WIRE_LEVEL_ENDPOINT
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|ActiveMQXAConnection
name|connection
decl_stmt|;
specifier|protected
name|XASession
name|xaSession
decl_stmt|;
specifier|protected
name|XAResource
name|xaRes
decl_stmt|;
specifier|private
specifier|final
name|String
name|xbean
init|=
literal|"xbean:"
decl_stmt|;
specifier|private
specifier|final
name|String
name|confBase
init|=
literal|"src/test/resources/org/apache/activemq/bugs/amq7067"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ActiveMQXAConnectionFactory
name|ACTIVE_MQ_CONNECTION_FACTORY
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ActiveMQConnectionFactory
name|ACTIVE_MQ_NON_XA_CONNECTION_FACTORY
decl_stmt|;
static|static
block|{
name|ACTIVE_MQ_CONNECTION_FACTORY
operator|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
name|WIRE_LEVEL_ENDPOINT
argument_list|)
expr_stmt|;
name|ACTIVE_MQ_NON_XA_CONNECTION_FACTORY
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|WIRE_LEVEL_ENDPOINT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|deleteData
argument_list|(
operator|new
name|File
argument_list|(
literal|"target/data"
argument_list|)
argument_list|)
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setupXAConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
operator|(
name|ActiveMQXAConnection
operator|)
name|ACTIVE_MQ_CONNECTION_FACTORY
operator|.
name|createXAConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|xaSession
operator|=
name|connection
operator|.
name|createXASession
argument_list|()
expr_stmt|;
name|xaRes
operator|=
name|xaSession
operator|.
name|getXAResource
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
name|xbean
operator|+
name|confBase
operator|+
literal|"/activemq.xml"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXAPrepare
parameter_list|()
throws|throws
name|Exception
block|{
name|setupXAConnection
argument_list|()
expr_stmt|;
name|Queue
name|holdKahaDb
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"holdKahaDb"
argument_list|)
decl_stmt|;
name|MessageProducer
name|holdKahaDbProducer
init|=
name|xaSession
operator|.
name|createProducer
argument_list|(
name|holdKahaDb
argument_list|)
decl_stmt|;
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****** create new txid = "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|start
argument_list|(
name|txid
argument_list|,
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|TextMessage
name|helloMessage
init|=
name|xaSession
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|holdKahaDbProducer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|end
argument_list|(
name|txid
argument_list|,
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
operator|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queue
argument_list|)
operator|)
operator|.
name|purge
argument_list|()
expr_stmt|;
name|Xid
index|[]
name|xids
init|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
decl_stmt|;
comment|//Should be 1 since we have only 1 prepared
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|setupXAConnection
argument_list|()
expr_stmt|;
name|xids
operator|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****** recovered = "
operator|+
name|xids
argument_list|)
expr_stmt|;
comment|// THIS SHOULD NOT FAIL AS THERE SHOULD DBE ONLY 1 TRANSACTION!
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXAcommit
parameter_list|()
throws|throws
name|Exception
block|{
name|setupXAConnection
argument_list|()
expr_stmt|;
name|Queue
name|holdKahaDb
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"holdKahaDb"
argument_list|)
decl_stmt|;
name|createDanglingTransaction
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|holdKahaDb
argument_list|)
expr_stmt|;
name|MessageProducer
name|holdKahaDbProducer
init|=
name|xaSession
operator|.
name|createProducer
argument_list|(
name|holdKahaDb
argument_list|)
decl_stmt|;
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****** create new txid = "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|start
argument_list|(
name|txid
argument_list|,
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|TextMessage
name|helloMessage
init|=
name|xaSession
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|holdKahaDbProducer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|end
argument_list|(
name|txid
argument_list|,
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|commit
argument_list|(
name|txid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
operator|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queue
argument_list|)
operator|)
operator|.
name|purge
argument_list|()
expr_stmt|;
name|Xid
index|[]
name|xids
init|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
decl_stmt|;
comment|//Should be 1 since we have only 1 prepared
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|setupXAConnection
argument_list|()
expr_stmt|;
name|xids
operator|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
expr_stmt|;
comment|// THIS SHOULD NOT FAIL AS THERE SHOULD DBE ONLY 1 TRANSACTION!
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXArollback
parameter_list|()
throws|throws
name|Exception
block|{
name|setupXAConnection
argument_list|()
expr_stmt|;
name|Queue
name|holdKahaDb
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"holdKahaDb"
argument_list|)
decl_stmt|;
name|createDanglingTransaction
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|holdKahaDb
argument_list|)
expr_stmt|;
name|MessageProducer
name|holdKahaDbProducer
init|=
name|xaSession
operator|.
name|createProducer
argument_list|(
name|holdKahaDb
argument_list|)
decl_stmt|;
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****** create new txid = "
operator|+
name|txid
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|start
argument_list|(
name|txid
argument_list|,
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|TextMessage
name|helloMessage
init|=
name|xaSession
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|holdKahaDbProducer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|end
argument_list|(
name|txid
argument_list|,
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|rollback
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|produce
argument_list|(
name|xaRes
argument_list|,
name|xaSession
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
operator|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|region
operator|.
name|Queue
operator|)
name|broker
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queue
argument_list|)
operator|)
operator|.
name|purge
argument_list|()
expr_stmt|;
name|Xid
index|[]
name|xids
init|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
decl_stmt|;
comment|//Should be 1 since we have only 1 prepared
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|setupXAConnection
argument_list|()
expr_stmt|;
name|xids
operator|=
name|xaRes
operator|.
name|recover
argument_list|(
name|TMSTARTRSCAN
argument_list|)
expr_stmt|;
comment|// THIS SHOULD NOT FAIL AS THERE SHOULD BE ONLY 1 TRANSACTION!
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|xids
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommit
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|connection
init|=
name|ACTIVE_MQ_NON_XA_CONNECTION_FACTORY
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Queue
name|holdKahaDb
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"holdKahaDb"
argument_list|)
decl_stmt|;
name|MessageProducer
name|holdKahaDbProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|holdKahaDb
argument_list|)
decl_stmt|;
name|TextMessage
name|helloMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|holdKahaDbProducer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|produce
argument_list|(
name|connection
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|produce
argument_list|(
name|connection
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"QueueSize %s: %d"
argument_list|,
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|getQueueSize
argument_list|(
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|purgeQueue
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|0
operator|==
name|getQueueSize
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// force gc
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|checkpoint
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|curruptIndexFile
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"QueueSize %s: %d"
argument_list|,
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|getQueueSize
argument_list|(
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// THIS SHOULD NOT FAIL AS THERE SHOULD BE ONLY 1 TRANSACTION!
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getQueueSize
argument_list|(
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRollback
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|connection
init|=
name|ACTIVE_MQ_NON_XA_CONNECTION_FACTORY
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Queue
name|holdKahaDb
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"holdKahaDb"
argument_list|)
decl_stmt|;
name|MessageProducer
name|holdKahaDbProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|holdKahaDb
argument_list|)
decl_stmt|;
name|TextMessage
name|helloMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|holdKahaDbProducer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|produce
argument_list|(
name|connection
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|produce
argument_list|(
name|connection
argument_list|,
name|queue
argument_list|,
literal|100
argument_list|,
literal|512
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"QueueSize %s: %d"
argument_list|,
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|getQueueSize
argument_list|(
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|purgeQueue
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|0
operator|==
name|getQueueSize
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// force gc
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|checkpoint
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|curruptIndexFile
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
comment|// no sign of the test queue on recovery, rollback is the default for any inflight
comment|// this test serves as a sanity check on existing behaviour
try|try
block|{
name|getQueueSize
argument_list|(
name|holdKahaDb
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expect InstanceNotFoundException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InstanceNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|createDanglingTransaction
parameter_list|(
name|XAResource
name|xaRes
parameter_list|,
name|XASession
name|xaSession
parameter_list|,
name|Queue
name|queue
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
throws|,
name|XAException
block|{
name|MessageProducer
name|producer
init|=
name|xaSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|XATransactionId
name|txId
init|=
name|createXATransaction
argument_list|()
decl_stmt|;
name|xaRes
operator|.
name|start
argument_list|(
name|txId
argument_list|,
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|TextMessage
name|helloMessage
init|=
name|xaSession
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"dangler"
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|end
argument_list|(
name|txId
argument_list|,
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|prepare
argument_list|(
name|txId
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"****** createDanglingTransaction txId = "
operator|+
name|txId
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|void
name|produce
parameter_list|(
name|XAResource
name|xaRes
parameter_list|,
name|XASession
name|xaSession
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|int
name|messageCount
parameter_list|,
name|int
name|messageSize
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
throws|,
name|XAException
block|{
name|MessageProducer
name|producer
init|=
name|xaSession
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|XATransactionId
name|txid
init|=
name|createXATransaction
argument_list|()
decl_stmt|;
name|xaRes
operator|.
name|start
argument_list|(
name|txid
argument_list|,
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|TextMessage
name|helloMessage
init|=
name|xaSession
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
name|messageSize
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|end
argument_list|(
name|txid
argument_list|,
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|prepare
argument_list|(
name|txid
argument_list|)
expr_stmt|;
name|xaRes
operator|.
name|commit
argument_list|(
name|txid
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|produce
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|int
name|messageCount
parameter_list|,
name|int
name|messageSize
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
throws|,
name|XAException
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|helloMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"a"
argument_list|,
name|messageSize
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|helloMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|XATransactionId
name|createXATransaction
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|XATransactionId
name|xid
init|=
operator|new
name|XATransactionId
argument_list|()
decl_stmt|;
name|xid
operator|.
name|setBranchQualifier
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|xid
operator|.
name|setGlobalTransactionId
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|xid
operator|.
name|setFormatId
argument_list|(
literal|55
argument_list|)
expr_stmt|;
return|return
name|xid
return|;
block|}
specifier|private
name|RecoveredXATransactionViewMBean
name|getProxyToPreparedTransactionViewMBean
parameter_list|(
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,transactionType=RecoveredXaTransaction,xid="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|xid
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RecoveredXATransactionViewMBean
name|proxy
init|=
operator|(
name|RecoveredXATransactionViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|RecoveredXATransactionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
specifier|private
name|PersistenceAdapterViewMBean
name|getProxyToPersistenceAdapter
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|JMSException
block|{
return|return
operator|(
name|PersistenceAdapterViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|BrokerMBeanSupport
operator|.
name|createPersistenceAdapterName
argument_list|(
name|broker
operator|.
name|getBrokerObjectName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|,
name|PersistenceAdapterViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|void
name|deleteData
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|entries
init|=
name|file
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|String
name|s
range|:
name|entries
control|)
block|{
name|File
name|currentFile
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentFile
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|deleteData
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
block|}
name|currentFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|private
name|long
name|getQueueSize
parameter_list|(
specifier|final
name|String
name|queueName
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
name|DestinationViewMBean
name|proxy
init|=
operator|(
name|DestinationViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|DestinationViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
operator|.
name|getQueueSize
argument_list|()
return|;
block|}
specifier|private
name|void
name|purgeQueue
parameter_list|(
specifier|final
name|String
name|queueName
parameter_list|)
throws|throws
name|MalformedObjectNameException
throws|,
name|Exception
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|JMXSupport
operator|.
name|encodeObjectNamePart
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|getDataDirectory
parameter_list|()
throws|throws
name|MalformedObjectNameException
block|{
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost"
argument_list|)
decl_stmt|;
name|BrokerViewMBean
name|proxy
init|=
operator|(
name|BrokerViewMBean
operator|)
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|proxy
operator|.
name|getDataDirectory
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|void
name|curruptIndexFile
parameter_list|(
specifier|final
name|String
name|dataPath
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnsupportedEncodingException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s/kahadb/db.data"
argument_list|,
name|dataPath
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"asdasdasd"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

