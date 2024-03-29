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
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * A facade for either a local in JVM broker or a remote broker over JMX  *  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerFacade
block|{
comment|/** 	 * The name of the active broker (f.e. 'localhost' or 'my broker'). 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|String
name|getBrokerName
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Admin view of the broker. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|BrokerViewMBean
name|getBrokerAdmin
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All queues known to the broker. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|QueueViewMBean
argument_list|>
name|getQueues
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All topics known to the broker. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|TopicViewMBean
argument_list|>
name|getTopics
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All active consumers of a queue. 	 *  	 * @param queueName 	 *            the name of the queue, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getQueueConsumers
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All active producers to a queue. 	 *  	 * @param queueName 	 *            the name of the queue, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|ProducerViewMBean
argument_list|>
name|getQueueProducers
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All active producers to a topic. 	 *  	 * @param queueName 	 *            the name of the topic, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|ProducerViewMBean
argument_list|>
name|getTopicProducers
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All active non-durable subscribers to a topic. 	 *  	 * @param queueName 	 *            the name of the topic, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getTopicSubscribers
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All active non-durable subscribers to a topic. 	 *  	 * @param queueName 	 *            the name of the topic, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getNonDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Active durable subscribers to topics of the broker. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|DurableSubscriptionViewMBean
argument_list|>
name|getDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Inactive durable subscribers to topics of the broker. 	 * 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|DurableSubscriptionViewMBean
argument_list|>
name|getInactiveDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * The names of all transport connectors of the broker (f.e. openwire, ssl) 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|String
argument_list|>
name|getConnectors
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * A transport connectors. 	 *  	 * @param name 	 *            name of the connector (f.e. openwire) 	 * @return<code>null</code> if not found 	 * @throws Exception 	 */
name|ConnectorViewMBean
name|getConnector
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * All connections to all transport connectors of the broker. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|ConnectionViewMBean
argument_list|>
name|getConnections
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * The names of all connections to a specific transport connectors of the 	 * broker. 	 *  	 * @see #getConnection(String) 	 * @param connectorName 	 *            not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|String
argument_list|>
name|getConnections
parameter_list|(
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * A specific connection to the broker. 	 *  	 * @param connectionName 	 *            the name of the connection, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|ConnectionViewMBean
name|getConnection
parameter_list|(
name|String
name|connectionName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Returns all consumers of a connection. 	 *  	 * @param connectionName 	 *            the name of the connection, not<code>null</code> 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getConsumersOnConnection
parameter_list|(
name|String
name|connectionName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * The brokers network connectors. 	 *  	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|NetworkConnectorViewMBean
argument_list|>
name|getNetworkConnectors
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * The brokers network bridges. 	 * 	 * @return not<code>null</code> 	 * @throws Exception 	 */
name|Collection
argument_list|<
name|NetworkBridgeViewMBean
argument_list|>
name|getNetworkBridges
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Purges the given destination 	 *  	 * @param destination 	 * @throws Exception 	 */
name|void
name|purgeQueue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Get the view of the queue with the specified name. 	 *  	 * @param name 	 *            not<code>null</code> 	 * @return<code>null</code> if no queue with this name exists 	 * @throws Exception 	 */
name|QueueViewMBean
name|getQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Get the view of the topic with the specified name. 	 *  	 * @param name 	 *            not<code>null</code> 	 * @return<code>null</code> if no topic with this name exists 	 * @throws Exception 	 */
name|TopicViewMBean
name|getTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** 	 * Get the JobScheduler MBean 	 * @return the jobScheduler or null if not configured 	 * @throws Exception 	 */
name|JobSchedulerViewMBean
name|getJobScheduler
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Get the JobScheduler MBean      * @return the jobScheduler or null if not configured      * @throws Exception      */
name|Collection
argument_list|<
name|JobFacade
argument_list|>
name|getScheduledJobs
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|boolean
name|isJobSchedulerStarted
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

