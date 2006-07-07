begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
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
name|apache
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
name|TwoBrokerTopicSendReceiveUsingHttpTest
extends|extends
name|TwoBrokerTopicSendReceiveTest
block|{
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// Give jetty server enough time to setup,
comment|// so we don't lose messages when connection fails
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
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
literal|"org/apache/activemq/usecases/receiver-http.xml"
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
literal|"org/apache/activemq/usecases/sender-http.xml"
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

