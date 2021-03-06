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
name|tool
operator|.
name|properties
package|;
end_package

begin_class
specifier|public
class|class
name|JmsClientProperties
extends|extends
name|AbstractObjectProperties
block|{
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_AUTO_ACKNOWLEDGE
init|=
literal|"autoAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_CLIENT_ACKNOWLEDGE
init|=
literal|"clientAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_DUPS_OK_ACKNOWLEDGE
init|=
literal|"dupsAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_TRANSACTED
init|=
literal|"transacted"
decl_stmt|;
specifier|protected
name|String
name|destName
init|=
literal|"TEST.FOO"
decl_stmt|;
specifier|protected
name|boolean
name|destComposite
decl_stmt|;
specifier|protected
name|String
name|sessAckMode
init|=
name|SESSION_AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|protected
name|boolean
name|sessTransacted
decl_stmt|;
comment|// commit transaction after X msgs only.
specifier|protected
name|int
name|commitAfterXMsgs
init|=
literal|1
decl_stmt|;
specifier|protected
name|String
name|jmsProvider
decl_stmt|;
specifier|protected
name|String
name|jmsVersion
decl_stmt|;
specifier|protected
name|String
name|jmsProperties
decl_stmt|;
specifier|public
name|String
name|getDestName
parameter_list|()
block|{
return|return
name|destName
return|;
block|}
specifier|public
name|void
name|setDestName
parameter_list|(
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDestComposite
parameter_list|()
block|{
return|return
name|destComposite
return|;
block|}
specifier|public
name|void
name|setDestComposite
parameter_list|(
name|boolean
name|destComposite
parameter_list|)
block|{
name|this
operator|.
name|destComposite
operator|=
name|destComposite
expr_stmt|;
block|}
specifier|public
name|String
name|getSessAckMode
parameter_list|()
block|{
return|return
name|sessAckMode
return|;
block|}
specifier|public
name|void
name|setSessAckMode
parameter_list|(
name|String
name|sessAckMode
parameter_list|)
block|{
name|this
operator|.
name|sessAckMode
operator|=
name|sessAckMode
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSessTransacted
parameter_list|()
block|{
return|return
name|sessTransacted
return|;
block|}
specifier|public
name|void
name|setSessTransacted
parameter_list|(
name|boolean
name|sessTransacted
parameter_list|)
block|{
name|this
operator|.
name|sessTransacted
operator|=
name|sessTransacted
expr_stmt|;
block|}
specifier|public
name|void
name|setCommitAfterXMsgs
parameter_list|(
name|int
name|commitAfterXMsg
parameter_list|)
block|{
name|this
operator|.
name|commitAfterXMsgs
operator|=
name|commitAfterXMsg
expr_stmt|;
block|}
specifier|public
name|int
name|getCommitAfterXMsgs
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitAfterXMsgs
return|;
block|}
specifier|public
name|String
name|getJmsProvider
parameter_list|()
block|{
return|return
name|jmsProvider
return|;
block|}
specifier|public
name|void
name|setJmsProvider
parameter_list|(
name|String
name|jmsProvider
parameter_list|)
block|{
name|this
operator|.
name|jmsProvider
operator|=
name|jmsProvider
expr_stmt|;
block|}
specifier|public
name|String
name|getJmsVersion
parameter_list|()
block|{
return|return
name|jmsVersion
return|;
block|}
specifier|public
name|void
name|setJmsVersion
parameter_list|(
name|String
name|jmsVersion
parameter_list|)
block|{
name|this
operator|.
name|jmsVersion
operator|=
name|jmsVersion
expr_stmt|;
block|}
specifier|public
name|String
name|getJmsProperties
parameter_list|()
block|{
return|return
name|jmsProperties
return|;
block|}
specifier|public
name|void
name|setJmsProperties
parameter_list|(
name|String
name|jmsProperties
parameter_list|)
block|{
name|this
operator|.
name|jmsProperties
operator|=
name|jmsProperties
expr_stmt|;
block|}
block|}
end_class

end_unit

