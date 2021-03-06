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
name|jms
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
name|JMSException
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
name|QueueSender
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * Used to store a pooled session instance and any resources that can  * be left open and carried along with the pooled instance such as the  * anonymous producer used for all MessageProducer instances created  * from this pooled session when enabled.  */
end_comment

begin_class
specifier|public
class|class
name|SessionHolder
block|{
specifier|private
specifier|final
name|Session
name|session
decl_stmt|;
specifier|private
specifier|volatile
name|MessageProducer
name|producer
decl_stmt|;
specifier|private
specifier|volatile
name|TopicPublisher
name|publisher
decl_stmt|;
specifier|private
specifier|volatile
name|QueueSender
name|sender
decl_stmt|;
specifier|public
name|SessionHolder
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
try|try
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|producer
operator|=
literal|null
expr_stmt|;
name|publisher
operator|=
literal|null
expr_stmt|;
name|sender
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
specifier|public
name|MessageProducer
name|getOrCreateProducer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|producer
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|producer
operator|==
literal|null
condition|)
block|{
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|producer
return|;
block|}
specifier|public
name|TopicPublisher
name|getOrCreatePublisher
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|publisher
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|publisher
operator|==
literal|null
condition|)
block|{
name|publisher
operator|=
operator|(
operator|(
name|TopicSession
operator|)
name|session
operator|)
operator|.
name|createPublisher
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|publisher
return|;
block|}
specifier|public
name|QueueSender
name|getOrCreateSender
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|sender
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|sender
operator|==
literal|null
condition|)
block|{
name|sender
operator|=
operator|(
operator|(
name|QueueSession
operator|)
name|session
operator|)
operator|.
name|createSender
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sender
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|session
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

