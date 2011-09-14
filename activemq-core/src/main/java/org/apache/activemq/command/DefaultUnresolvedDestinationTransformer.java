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
name|command
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
name|Queue
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultUnresolvedDestinationTransformer
implements|implements
name|UnresolvedDestinationTransformer
block|{
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|transform
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|queueName
init|=
operator|(
operator|(
name|Queue
operator|)
name|dest
operator|)
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|String
name|topicName
init|=
operator|(
operator|(
name|Topic
operator|)
name|dest
operator|)
operator|.
name|getTopicName
argument_list|()
decl_stmt|;
if|if
condition|(
name|queueName
operator|==
literal|null
operator|&&
name|topicName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Unresolvable destination: Both queue and topic names are null: "
operator|+
name|dest
argument_list|)
throw|;
block|}
try|try
block|{
name|Method
name|isQueueMethod
init|=
name|dest
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"isQueue"
argument_list|)
decl_stmt|;
name|Method
name|isTopicMethod
init|=
name|dest
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"isTopic"
argument_list|)
decl_stmt|;
name|Boolean
name|isQueue
init|=
operator|(
name|Boolean
operator|)
name|isQueueMethod
operator|.
name|invoke
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Boolean
name|isTopic
init|=
operator|(
name|Boolean
operator|)
name|isTopicMethod
operator|.
name|invoke
argument_list|(
name|dest
argument_list|)
decl_stmt|;
if|if
condition|(
name|isQueue
condition|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isTopic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Unresolvable destination: Neither Queue nor Topic: "
operator|+
name|dest
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Unresolvable destination: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|": "
operator|+
name|dest
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|transform
parameter_list|(
name|String
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|dest
argument_list|)
return|;
block|}
block|}
end_class

end_unit

