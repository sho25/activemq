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
operator|.
name|retroactive
package|;
end_package

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
name|TextMessage
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

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|RetroactiveConsumerTestWithLastImagePolicyWithWildcardTest
extends|extends
name|RetroactiveConsumerTestWithSimpleMessageListTest
block|{
specifier|private
name|int
name|counter
init|=
literal|1
decl_stmt|;
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|MessageProducer
name|producer
parameter_list|,
name|TextMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQTopic
name|topic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|destination
operator|.
name|toString
argument_list|()
operator|+
literal|"."
operator|+
operator|(
name|counter
operator|++
operator|)
argument_list|)
decl_stmt|;
comment|//        System.out.println("Sending to destination: " + topic);
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageProducer
name|createProducer
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|destination
operator|.
name|toString
argument_list|()
operator|+
literal|".>"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getBrokerXml
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/test/retroactive/activemq-lastimage-policy.xml"
return|;
block|}
block|}
end_class

end_unit

