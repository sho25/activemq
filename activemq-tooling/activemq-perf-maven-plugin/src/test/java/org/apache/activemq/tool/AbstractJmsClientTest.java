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
name|assertEquals
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|JmsClientProperties
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

begin_class
specifier|public
class|class
name|AbstractJmsClientTest
block|{
specifier|public
class|class
name|NullJmsClient
extends|extends
name|AbstractJmsClient
block|{
specifier|private
name|JmsClientProperties
name|client
decl_stmt|;
specifier|public
name|NullJmsClient
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JmsClientProperties
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setClient
parameter_list|(
name|JmsClientProperties
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
block|}
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
name|JmsClientProperties
name|clientProperties
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
name|NullJmsClient
argument_list|(
name|connectionFactory
argument_list|)
expr_stmt|;
name|clientProperties
operator|=
operator|new
name|JmsClientProperties
argument_list|()
expr_stmt|;
name|clientProperties
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
name|clientProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestination
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
literal|"dest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestination_topic
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
literal|"topic://dest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestination_queue
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
literal|"queue://dest"
argument_list|)
argument_list|)
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
name|assertDestinationType
argument_list|(
name|TEMP_QUEUE_TYPE
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
name|assertDestinationType
argument_list|(
name|TEMP_TOPIC_TYPE
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
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_commaSeparated
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestName
argument_list|(
literal|"queue://foo,topic://cheese"
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"foo"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"cheese"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_multipleComposite
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestComposite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clientProperties
operator|.
name|setDestName
argument_list|(
literal|"queue://foo,queue://cheese"
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// suffixes should be added
name|assertDestinationNameType
argument_list|(
literal|"foo,cheese"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations
parameter_list|()
throws|throws
name|JMSException
block|{
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
name|DEFAULT_DEST
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_multiple
parameter_list|()
throws|throws
name|JMSException
block|{
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// suffixes should be added
name|assertDestinationNameType
argument_list|(
name|DEFAULT_DEST
operator|+
literal|".0"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
name|DEFAULT_DEST
operator|+
literal|".1"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_multipleCommaSeparated
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestName
argument_list|(
literal|"queue://foo,topic://cheese"
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// suffixes should be added
name|assertDestinationNameType
argument_list|(
literal|"foo.0"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"foo.1"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"cheese.0"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"cheese.1"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_composite
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestComposite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// suffixes should be added
name|String
name|expectedDestName
init|=
name|DEFAULT_DEST
operator|+
literal|".0,"
operator|+
name|DEFAULT_DEST
operator|+
literal|".1"
decl_stmt|;
name|assertDestinationNameType
argument_list|(
name|expectedDestName
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_compositeQueue
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestComposite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clientProperties
operator|.
name|setDestName
argument_list|(
literal|"queue://"
operator|+
name|DEFAULT_DEST
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// suffixes should be added
name|String
name|expectedDestName
init|=
name|DEFAULT_DEST
operator|+
literal|".0,"
operator|+
name|DEFAULT_DEST
operator|+
literal|".1"
decl_stmt|;
name|assertDestinationNameType
argument_list|(
name|expectedDestName
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateDestinations_compositeCommaSeparated
parameter_list|()
throws|throws
name|JMSException
block|{
name|clientProperties
operator|.
name|setDestComposite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clientProperties
operator|.
name|setDestName
argument_list|(
literal|"queue://foo,topic://cheese"
argument_list|)
expr_stmt|;
name|Destination
index|[]
name|destinations
init|=
name|jmsClient
operator|.
name|createDestinations
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|destinations
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"foo.0,foo.1"
argument_list|,
name|QUEUE_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertDestinationNameType
argument_list|(
literal|"cheese.0,cheese.1"
argument_list|,
name|TOPIC_TYPE
argument_list|,
name|asAmqDest
argument_list|(
name|destinations
index|[
literal|1
index|]
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
name|void
name|assertDestinationType
parameter_list|(
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

