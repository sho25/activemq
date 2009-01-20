begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|test
package|;
end_package

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
name|ConnectionConsumer
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
name|DeliveryMode
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
name|MessageConsumer
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
name|ServerSessionPool
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
name|jms
operator|.
name|Topic
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

begin_comment
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsResourceProvider
block|{
specifier|private
name|String
name|serverUri
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|private
name|boolean
name|transacted
decl_stmt|;
specifier|private
name|int
name|ackMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|private
name|boolean
name|isTopic
decl_stmt|;
specifier|private
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|private
name|String
name|durableName
init|=
literal|"DummyName"
decl_stmt|;
specifier|private
name|String
name|clientID
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**      * Creates a connection factory.      *       * @see org.apache.activemq.test.JmsResourceProvider#createConnectionFactory()      */
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|serverUri
argument_list|)
return|;
block|}
comment|/**      * Creates a connection.      *       * @see org.apache.activemq.test.JmsResourceProvider#createConnection(javax.jms.ConnectionFactory)      */
specifier|public
name|Connection
name|createConnection
parameter_list|(
name|ConnectionFactory
name|cf
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|getClientID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|getClientID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|connection
return|;
block|}
comment|/**      * @see org.apache.activemq.test.JmsResourceProvider#createSession(javax.jms.Connection)      */
specifier|public
name|Session
name|createSession
parameter_list|(
name|Connection
name|conn
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|conn
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
return|;
block|}
comment|/**      * @see org.apache.activemq.test.JmsResourceProvider#createConsumer(javax.jms.Session,      *      javax.jms.Destination)      */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|isDurableSubscriber
argument_list|()
condition|)
block|{
return|return
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|durableName
argument_list|)
return|;
block|}
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/**      * Creates a connection for a consumer.      *       * @param ssp - ServerSessionPool      * @return ConnectionConsumer      */
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|ServerSessionPool
name|ssp
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|connection
operator|.
name|createConnectionConsumer
argument_list|(
name|destination
argument_list|,
literal|null
argument_list|,
name|ssp
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**      * Creates a producer.      *       * @see org.apache.activemq.test.JmsResourceProvider#createProducer(javax.jms.Session,      *      javax.jms.Destination)      */
specifier|public
name|MessageProducer
name|createProducer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
comment|/**      * Creates a destination, which can either a topic or a queue.      *       * @see org.apache.activemq.test.JmsResourceProvider#createDestination(javax.jms.Session,      *      java.lang.String)      */
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|isTopic
condition|)
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
literal|"TOPIC."
operator|+
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createQueue
argument_list|(
literal|"QUEUE."
operator|+
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns true if the subscriber is durable.      *       * @return isDurableSubscriber      */
specifier|public
name|boolean
name|isDurableSubscriber
parameter_list|()
block|{
return|return
name|isTopic
operator|&&
name|durableName
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the acknowledgement mode.      *       * @return Returns the ackMode.      */
specifier|public
name|int
name|getAckMode
parameter_list|()
block|{
return|return
name|ackMode
return|;
block|}
comment|/**      * Sets the acnknowledgement mode.      *       * @param ackMode The ackMode to set.      */
specifier|public
name|void
name|setAckMode
parameter_list|(
name|int
name|ackMode
parameter_list|)
block|{
name|this
operator|.
name|ackMode
operator|=
name|ackMode
expr_stmt|;
block|}
comment|/**      * Returns true if the destination is a topic, false if the destination is a      * queue.      *       * @return Returns the isTopic.      */
specifier|public
name|boolean
name|isTopic
parameter_list|()
block|{
return|return
name|isTopic
return|;
block|}
comment|/**      * @param isTopic The isTopic to set.      */
specifier|public
name|void
name|setTopic
parameter_list|(
name|boolean
name|isTopic
parameter_list|)
block|{
name|this
operator|.
name|isTopic
operator|=
name|isTopic
expr_stmt|;
block|}
comment|/**      * Returns the server URI.      *       * @return Returns the serverUri.      */
specifier|public
name|String
name|getServerUri
parameter_list|()
block|{
return|return
name|serverUri
return|;
block|}
comment|/**      * Sets the server URI.      *       * @param serverUri - the server URI to set.      */
specifier|public
name|void
name|setServerUri
parameter_list|(
name|String
name|serverUri
parameter_list|)
block|{
name|this
operator|.
name|serverUri
operator|=
name|serverUri
expr_stmt|;
block|}
comment|/**      * Return true if the session is transacted.      *       * @return Returns the transacted.      */
specifier|public
name|boolean
name|isTransacted
parameter_list|()
block|{
return|return
name|transacted
return|;
block|}
comment|/**      * Sets the session to be transacted.      *       * @param transacted      */
specifier|public
name|void
name|setTransacted
parameter_list|(
name|boolean
name|transacted
parameter_list|)
block|{
name|this
operator|.
name|transacted
operator|=
name|transacted
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|setAckMode
argument_list|(
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the delivery mode.      *       * @return deliveryMode      */
specifier|public
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|deliveryMode
return|;
block|}
comment|/**      * Sets the delivery mode.      *       * @param deliveryMode      */
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|deliveryMode
parameter_list|)
block|{
name|this
operator|.
name|deliveryMode
operator|=
name|deliveryMode
expr_stmt|;
block|}
comment|/**      * Returns the client id.      *       * @return clientID      */
specifier|public
name|String
name|getClientID
parameter_list|()
block|{
return|return
name|clientID
return|;
block|}
comment|/**      * Sets the client id.      *       * @param clientID      */
specifier|public
name|void
name|setClientID
parameter_list|(
name|String
name|clientID
parameter_list|)
block|{
name|this
operator|.
name|clientID
operator|=
name|clientID
expr_stmt|;
block|}
comment|/**      * Returns the durable name of the provider.      *       * @return durableName      */
specifier|public
name|String
name|getDurableName
parameter_list|()
block|{
return|return
name|durableName
return|;
block|}
comment|/**      * Sets the durable name of the provider.      *       * @param durableName      */
specifier|public
name|void
name|setDurableName
parameter_list|(
name|String
name|durableName
parameter_list|)
block|{
name|this
operator|.
name|durableName
operator|=
name|durableName
expr_stmt|;
block|}
block|}
end_class

end_unit

