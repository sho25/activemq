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
name|broker
operator|.
name|virtual
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
name|xbean
operator|.
name|XBeanBrokerFactory
import|;
end_import

begin_comment
comment|/**  *  *   */
end_comment

begin_class
specifier|public
class|class
name|VirtualTopicPubSubUsingXBeanTest
extends|extends
name|VirtualTopicPubSubTest
block|{
specifier|protected
name|String
name|getVirtualTopicConsumerName
parameter_list|()
block|{
return|return
literal|"VirtualTopicConsumers.ConsumerNumberOne.FOO"
return|;
block|}
specifier|protected
name|String
name|getVirtualTopicName
parameter_list|()
block|{
return|return
literal|"FOO"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|XBeanBrokerFactory
name|factory
init|=
operator|new
name|XBeanBrokerFactory
argument_list|()
decl_stmt|;
name|BrokerService
name|answer
init|=
name|factory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|getBrokerConfigUri
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// lets disable persistence as we are a test
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|String
name|getBrokerConfigUri
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/virtual/global-virtual-topics.xml"
return|;
block|}
block|}
end_class

end_unit

