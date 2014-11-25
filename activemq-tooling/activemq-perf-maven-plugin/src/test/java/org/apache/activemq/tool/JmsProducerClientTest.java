begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
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
name|*
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
name|command
operator|.
name|ActiveMQDestination
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
name|tool
operator|.
name|properties
operator|.
name|JmsProducerProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_class
specifier|public
class|class
name|JmsProducerClientTest
block|{
specifier|private
specifier|final
name|String
name|DEFAULT_DEST
init|=
literal|"TEST.FOO"
decl_stmt|;
specifier|private
specifier|static
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|static
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|AbstractJmsClient
name|jmsClient
decl_stmt|;
specifier|private
name|JmsProducerProperties
name|producerProperties
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBrokerAndConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost?persistent=false"
argument_list|)
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|jmsClient
operator|=
operator|new
name|JmsProducerClient
argument_list|(
name|connectionFactory
argument_list|)
expr_stmt|;
name|producerProperties
operator|=
operator|new
name|JmsProducerProperties
argument_list|()
expr_stmt|;
name|producerProperties
operator|.
name|setDestName
argument_list|(
name|DEFAULT_DEST
argument_list|)
expr_stmt|;
name|jmsClient
operator|.
name|setClient
argument_list|(
name|producerProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestination_tempQueue
parameter_list|()
throws|throws
name|JMSException
block|{
name|assertDestinationNameType
argument_list|(
literal|"dest"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|jmsClient
operator|.
name|createDestination
argument_list|(
literal|"temp-queue://dest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestination_tempTopic
parameter_list|()
throws|throws
name|JMSException
block|{
name|assertDestinationNameType
argument_list|(
literal|"dest"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|jmsClient
operator|.
name|createDestination
argument_list|(
literal|"temp-topic://dest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertDestinationNameType
parameter_list|(
name|String
name|physicalName
parameter_list|,
name|byte
name|destinationType
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|destinationType
argument_list|,
name|destination
operator|.
name|getDestinationType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|physicalName
argument_list|,
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ActiveMQDestination
name|asAmqDest
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
operator|(
name|ActiveMQDestination
operator|)
name|destination
return|;
block|}
block|}
end_class

end_unit

