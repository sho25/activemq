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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertNotNull
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
name|assertThat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
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
name|CompositeDataConstants
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
name|command
operator|.
name|ActiveMQQueue
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

begin_class
specifier|public
class|class
name|AMQ4530Test
block|{
specifier|private
specifier|static
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|static
name|String
name|TEST_QUEUE
init|=
literal|"testQueue"
decl_stmt|;
specifier|private
specifier|static
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|TEST_QUEUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|BROKER_ADDRESS
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
specifier|static
name|String
name|KEY
init|=
literal|"testproperty"
decl_stmt|;
specifier|private
specifier|static
name|String
name|VALUE
init|=
literal|"propvalue"
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connectionUri
operator|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|BROKER_ADDRESS
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Connection
name|conn
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|session
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|Destination
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|TEST_QUEUE
argument_list|)
decl_stmt|;
specifier|final
name|Message
name|toSend
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|toSend
operator|.
name|setStringProperty
argument_list|(
name|KEY
argument_list|,
name|VALUE
argument_list|)
expr_stmt|;
specifier|final
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
name|producer
operator|.
name|send
argument_list|(
name|queue
argument_list|,
name|toSend
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testStringPropertiesFromCompositeData
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|QueueViewMBean
name|queueView
init|=
name|getProxyToQueueViewMBean
argument_list|()
decl_stmt|;
specifier|final
name|CompositeData
name|message
init|=
name|queueView
operator|.
name|browse
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|TabularDataSupport
name|stringProperties
init|=
operator|(
name|TabularDataSupport
operator|)
name|message
operator|.
name|get
argument_list|(
name|CompositeDataConstants
operator|.
name|STRING_PROPERTIES
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stringProperties
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stringProperties
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|compositeDataEntry
init|=
operator|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|stringProperties
operator|.
name|entrySet
argument_list|()
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|CompositeData
name|stringEntry
init|=
operator|(
name|CompositeData
operator|)
name|compositeDataEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|stringEntry
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|stringEntry
operator|.
name|get
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|QueueViewMBean
name|getProxyToQueueViewMBean
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|NullPointerException
throws|,
name|JMSException
block|{
specifier|final
name|ObjectName
name|queueViewMBeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="
operator|+
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|QueueViewMBean
name|proxy
init|=
operator|(
name|QueueViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|queueViewMBeanName
argument_list|,
name|QueueViewMBean
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
block|}
end_class

end_unit

