begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 LogicBlaze, Inc. (http://www.logicblaze.com)  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|BytesMessage
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
name|MapMessage
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
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activecluster
operator|.
name|election
operator|.
name|ElectionStrategy
import|;
end_import

begin_comment
comment|/**  * Represents a logical connection to a cluster. From this object you can  * obtain the destination to send messages to, view the members of the cluster,  * watch cluster events (nodes joining, leaving, updating their state) as well  * as viewing each members state.  *<p/>  * You may also update the local node's state.  *  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cluster
extends|extends
name|Service
block|{
comment|/**      * Returns the destination used to send a message to all members of the cluster      *      * @return the destination to send messages to all members of the cluster      */
specifier|public
name|String
name|getDestination
parameter_list|()
function_decl|;
comment|/**      * A snapshot of the nodes in the cluster indexed by the Destination      * @return a Map containing all the nodes in the cluster, where key=node destination,value=node      */
specifier|public
name|Map
name|getNodes
parameter_list|()
function_decl|;
comment|/**      * Adds a new listener to cluster events      *      * @param listener      */
specifier|public
name|void
name|addClusterListener
parameter_list|(
name|ClusterListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Removes a listener to cluster events      *      * @param listener      */
specifier|public
name|void
name|removeClusterListener
parameter_list|(
name|ClusterListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * The local Node which allows you to mutate the state or subscribe to the      * nodes temporary queue for inbound messages direct to the Node      * @return the Node representing this peer in the cluster      */
specifier|public
name|LocalNode
name|getLocalNode
parameter_list|()
function_decl|;
comment|/**      * Allows overriding of the default election strategy with a custom      * implementation.      * @param strategy       */
specifier|public
name|void
name|setElectionStrategy
parameter_list|(
name|ElectionStrategy
name|strategy
parameter_list|)
function_decl|;
comment|// Messaging helper methods
comment|//-------------------------------------------------------------------------
comment|/**      * Sends a message to a destination, which could be to the entire group      * or could be a single Node's destination      *      * @param destination is either the group topic or a node's destination      * @param message     the message to be sent      * @throws JMSException      */
specifier|public
name|void
name|send
parameter_list|(
name|String
name|destination
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Utility method for sending back replies in message exchanges      *      * @param replyTo the replyTo JMS Destination on a Message      * @param message     the message to be sent      * @throws JMSException      */
specifier|public
name|void
name|send
parameter_list|(
name|Destination
name|replyTo
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a consumer of all the messags sent to the given destination,      * including messages sent via the send() messages      *      * @param destination      * @return a newly  created message consumer      * @throws JMSException      */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|destination
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a consumer of all message sent to the given destination,      * including messages sent via the send() message with an optional SQL 92 based selector to filter      * messages      *      * @param destination      * @param selector      * @return a newly  created message consumer      * @throws JMSException      */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a consumer of all message sent to the given destination,      * including messages sent via the send() message with an optional SQL 92 based selector to filter      * messages along with optionally ignoring local traffic - messages sent via the send()      * method on this object.      *      * @param destination the destination to consume from      * @param selector    an optional SQL 92 filter of messages which could be null      * @param noLocal     which if true messages sent via send() on this object will not be delivered to the consumer      * @return a newly  created message consumer      * @throws JMSException      */
specifier|public
name|MessageConsumer
name|createConsumer
parameter_list|(
name|String
name|destination
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|// Message factory methods
comment|//-------------------------------------------------------------------------
comment|/**      * Creates a new message without a body      * @return the create  Message      *      * @throws JMSException      */
specifier|public
name|Message
name|createMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new bytes message      * @return the create BytesMessage      *      * @throws JMSException      */
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link MapMessage}      * @return the created MapMessage      *      * @throws JMSException      */
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link ObjectMessage}      * @return the created ObjectMessage      *      * @throws JMSException      */
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link ObjectMessage}      *      * @param object      * @return the createdObjectMessage      * @throws JMSException      */
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|(
name|Serializable
name|object
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link StreamMessage}      * @return the create StreamMessage      *      * @throws JMSException      */
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link TextMessage}      * @return the create TextMessage      *      * @throws JMSException      */
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new {@link TextMessage}      *      * @param text      * @return the create TextMessage      * @throws JMSException      */
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Create a named Destination      * @param name      * @return the Destinatiion       * @throws JMSException      */
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * wait until a the cardimality of the cluster is reaches the expected count. This method will return false if the      * cluster isn't started or stopped while waiting      *      * @param expectedCount the number of expected members of a cluster      * @param timeout       timeout in milliseconds      * @return true if the cluster is fully connected      * @throws InterruptedException      */
name|boolean
name|waitForClusterToComplete
parameter_list|(
name|int
name|expectedCount
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

