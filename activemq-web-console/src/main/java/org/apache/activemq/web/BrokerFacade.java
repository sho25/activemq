begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * A facade for either a local in JVM broker or a remote broker over JMX  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerFacade
block|{
name|BrokerViewMBean
name|getBrokerAdmin
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|Collection
name|getQueues
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|Collection
name|getTopics
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|Collection
name|getDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Purges the given destination      * @param destination      * @throws Exception      */
name|void
name|purgeQueue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

