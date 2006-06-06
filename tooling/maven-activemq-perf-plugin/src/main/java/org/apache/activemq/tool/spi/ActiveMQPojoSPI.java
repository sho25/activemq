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
name|tool
operator|.
name|spi
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
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQPojoSPI
implements|implements
name|SPIConnectionFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_BROKER_URL
init|=
literal|"factory.brokerUrl"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_USERNAME
init|=
literal|"factory.username"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PASSWORD
init|=
literal|"factory.password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CLIENT_ID
init|=
literal|"factory.clientID"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ASYNC_SEND
init|=
literal|"factory.asyncSend"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ASYNC_DISPATCH
init|=
literal|"factory.asyncDispatch"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ASYNC_SESSION
init|=
literal|"factory.asyncSession"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CLOSE_TIMEOUT
init|=
literal|"factory.closeTimeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_COPY_MSG_ON_SEND
init|=
literal|"factory.copyMsgOnSend"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_DISABLE_TIMESTAMP
init|=
literal|"factory.disableTimestamp"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_DEFER_OBJ_SERIAL
init|=
literal|"factory.deferObjSerial"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ON_SEND_PREP_MSG
init|=
literal|"factory.onSendPrepMsg"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_OPTIM_ACK
init|=
literal|"factory.optimAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_OPTIM_DISPATCH
init|=
literal|"factory.optimDispatch"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PREFETCH_QUEUE
init|=
literal|"factory.prefetchQueue"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PREFETCH_TOPIC
init|=
literal|"factory.prefetchTopic"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_USE_COMPRESSION
init|=
literal|"factory.useCompression"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_USE_RETROACTIVE
init|=
literal|"factory.useRetroactive"
decl_stmt|;
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|configureConnectionFactory
argument_list|(
name|factory
argument_list|,
name|settings
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|configureConnectionFactory
parameter_list|(
name|ConnectionFactory
name|jmsFactory
parameter_list|,
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|jmsFactory
decl_stmt|;
name|String
name|setting
decl_stmt|;
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_BROKER_URL
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setBrokerURL
argument_list|(
name|setting
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_USERNAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setUserName
argument_list|(
name|setting
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_PASSWORD
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setPassword
argument_list|(
name|setting
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_CLIENT_ID
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setClientID
argument_list|(
name|setting
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_ASYNC_SEND
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setUseAsyncSend
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_ASYNC_DISPATCH
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setAsyncDispatch
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_ASYNC_SESSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setAlwaysSessionAsync
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_CLOSE_TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setCloseTimeout
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_COPY_MSG_ON_SEND
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setCopyMessageOnSend
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_DISABLE_TIMESTAMP
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setDisableTimeStampsByDefault
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_DEFER_OBJ_SERIAL
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setObjectMessageSerializationDefered
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_ON_SEND_PREP_MSG
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setOnSendPrepareMessageBody
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_OPTIM_ACK
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setOptimizeAcknowledge
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_OPTIM_DISPATCH
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setOptimizedMessageDispatch
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_PREFETCH_QUEUE
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_PREFETCH_TOPIC
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setTopicPrefetch
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_USE_COMPRESSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setUseCompression
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setting
operator|=
name|settings
operator|.
name|getProperty
argument_list|(
name|KEY_USE_RETROACTIVE
argument_list|)
expr_stmt|;
if|if
condition|(
name|setting
operator|!=
literal|null
operator|&&
name|setting
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factory
operator|.
name|setUseRetroactiveConsumer
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|setting
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

