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
name|console
operator|.
name|filter
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|QueueBrowser
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
name|command
operator|.
name|ActiveMQQueue
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
name|ActiveMQTopic
import|;
end_import

begin_class
specifier|public
class|class
name|AmqMessagesQueryFilter
extends|extends
name|AbstractQueryFilter
block|{
specifier|private
name|URI
name|brokerUrl
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
comment|/**      * Create a JMS message query filter      *       * @param brokerUrl - broker url to connect to      * @param destination - JMS destination to query      */
specifier|public
name|AmqMessagesQueryFilter
parameter_list|(
name|URI
name|brokerUrl
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * Queries the specified destination using the message selector format query      *       * @param queries - message selector queries      * @return list messages that matches the selector      * @throws Exception      */
specifier|public
name|List
name|query
parameter_list|(
name|List
name|queries
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|selector
init|=
literal|""
decl_stmt|;
comment|// Convert to message selector
for|for
control|(
name|Iterator
name|i
init|=
name|queries
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|selector
operator|=
name|selector
operator|+
literal|"("
operator|+
name|i
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|") AND "
expr_stmt|;
block|}
comment|// Remove last AND
if|if
condition|(
operator|!
name|selector
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|selector
operator|=
name|selector
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|selector
operator|.
name|length
argument_list|()
operator|-
literal|5
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQQueue
condition|)
block|{
return|return
name|queryMessages
argument_list|(
operator|(
name|ActiveMQQueue
operator|)
name|destination
argument_list|,
name|selector
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|queryMessages
argument_list|(
operator|(
name|ActiveMQTopic
operator|)
name|destination
argument_list|,
name|selector
argument_list|)
return|;
block|}
block|}
comment|/**      * Query the messages of a queue destination using a queue browser      *       * @param queue - queue destination      * @param selector - message selector      * @return list of messages that matches the selector      * @throws Exception      */
specifier|protected
name|List
name|queryMessages
parameter_list|(
name|ActiveMQQueue
name|queue
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|conn
init|=
name|createConnection
argument_list|(
name|getBrokerUrl
argument_list|()
argument_list|)
decl_stmt|;
name|Session
name|sess
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
name|QueueBrowser
name|browser
init|=
name|sess
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|,
name|selector
argument_list|)
decl_stmt|;
name|List
name|messages
init|=
name|Collections
operator|.
name|list
argument_list|(
name|browser
operator|.
name|getEnumeration
argument_list|()
argument_list|)
decl_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|messages
return|;
block|}
comment|/**      * Query the messages of a topic destination using a message consumer      *       * @param topic - topic destination      * @param selector - message selector      * @return list of messages that matches the selector      * @throws Exception      */
specifier|protected
name|List
name|queryMessages
parameter_list|(
name|ActiveMQTopic
name|topic
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
comment|// TODO: should we use a durable subscriber or a retroactive non-durable
comment|// subscriber?
comment|// TODO: if a durable subscriber is used, how do we manage it?
comment|// subscribe/unsubscribe tasks?
return|return
literal|null
return|;
block|}
comment|/**      * Create and start a JMS connection      *       * @param brokerUrl - broker url to connect to.      * @return JMS connection      * @throws JMSException      */
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|URI
name|brokerUrl
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|conn
init|=
operator|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerUrl
argument_list|)
operator|)
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
comment|/**      * Get the broker url being used.      *       * @return broker url      */
specifier|public
name|URI
name|getBrokerUrl
parameter_list|()
block|{
return|return
name|brokerUrl
return|;
block|}
comment|/**      * Set the broker url to use.      *       * @param brokerUrl - broker url      */
specifier|public
name|void
name|setBrokerUrl
parameter_list|(
name|URI
name|brokerUrl
parameter_list|)
block|{
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
block|}
comment|/**      * Get the destination being used.      *       * @return - JMS destination      */
specifier|public
name|Destination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
comment|/**      * Set the destination to use.      *       * @param destination - JMS destination      */
specifier|public
name|void
name|setDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
block|}
end_class

end_unit

