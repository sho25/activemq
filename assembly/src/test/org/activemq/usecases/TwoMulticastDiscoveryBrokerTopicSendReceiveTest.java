begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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

begin_comment
comment|/**  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|TwoMulticastDiscoveryBrokerTopicSendReceiveTest
extends|extends
name|TwoBrokerTopicSendReceiveTest
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|createReceiverConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnectionFactory
argument_list|(
literal|"org/activemq/usecases/receiver-discovery.xml"
argument_list|,
literal|"receiver"
argument_list|,
literal|"vm://receiver"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createSenderConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|createConnectionFactory
argument_list|(
literal|"org/activemq/usecases/sender-discovery.xml"
argument_list|,
literal|"sender"
argument_list|,
literal|"vm://sender"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

