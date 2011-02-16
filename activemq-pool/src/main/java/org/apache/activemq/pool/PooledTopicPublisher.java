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
name|pool
package|;
end_package

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
name|Message
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
name|TopicPublisher
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
name|ActiveMQTopicPublisher
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|PooledTopicPublisher
extends|extends
name|PooledProducer
implements|implements
name|TopicPublisher
block|{
specifier|public
name|PooledTopicPublisher
parameter_list|(
name|ActiveMQTopicPublisher
name|messageProducer
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|messageProducer
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Topic
name|getTopic
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getTopicPublisher
argument_list|()
operator|.
name|getTopic
argument_list|()
return|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|getTopicPublisher
argument_list|()
operator|.
name|publish
argument_list|(
operator|(
name|Topic
operator|)
name|getDestination
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|i1
parameter_list|,
name|long
name|l
parameter_list|)
throws|throws
name|JMSException
block|{
name|getTopicPublisher
argument_list|()
operator|.
name|publish
argument_list|(
operator|(
name|Topic
operator|)
name|getDestination
argument_list|()
argument_list|,
name|message
argument_list|,
name|i
argument_list|,
name|i1
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|getTopicPublisher
argument_list|()
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|Message
name|message
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|i1
parameter_list|,
name|long
name|l
parameter_list|)
throws|throws
name|JMSException
block|{
name|getTopicPublisher
argument_list|()
operator|.
name|publish
argument_list|(
name|topic
argument_list|,
name|message
argument_list|,
name|i
argument_list|,
name|i1
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQTopicPublisher
name|getTopicPublisher
parameter_list|()
block|{
return|return
operator|(
name|ActiveMQTopicPublisher
operator|)
name|getMessageProducer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

