begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
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
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicSendReceiveSubscriberTest
extends|extends
name|JmsTopicSendReceiveTest
block|{
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|durable
condition|)
block|{
return|return
name|super
operator|.
name|createConsumer
argument_list|()
return|;
block|}
else|else
block|{
name|TopicSession
name|topicSession
init|=
operator|(
name|TopicSession
operator|)
name|session
decl_stmt|;
return|return
name|topicSession
operator|.
name|createSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|consumerDestination
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

