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

begin_comment
comment|/**  * A cache key for the session details used to locate PooledSession intances.  */
end_comment

begin_class
specifier|public
class|class
name|SessionKey
block|{
specifier|private
specifier|final
name|boolean
name|transacted
decl_stmt|;
specifier|private
specifier|final
name|int
name|ackMode
decl_stmt|;
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|SessionKey
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|ackMode
parameter_list|)
block|{
name|this
operator|.
name|transacted
operator|=
name|transacted
expr_stmt|;
name|this
operator|.
name|ackMode
operator|=
name|ackMode
expr_stmt|;
name|hash
operator|=
name|ackMode
expr_stmt|;
if|if
condition|(
name|transacted
condition|)
block|{
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
literal|1
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|that
operator|instanceof
name|SessionKey
condition|)
block|{
return|return
name|equals
argument_list|(
operator|(
name|SessionKey
operator|)
name|that
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|SessionKey
name|that
parameter_list|)
block|{
return|return
name|this
operator|.
name|transacted
operator|==
name|that
operator|.
name|transacted
operator|&&
name|this
operator|.
name|ackMode
operator|==
name|that
operator|.
name|ackMode
return|;
block|}
specifier|public
name|boolean
name|isTransacted
parameter_list|()
block|{
return|return
name|transacted
return|;
block|}
specifier|public
name|int
name|getAckMode
parameter_list|()
block|{
return|return
name|ackMode
return|;
block|}
block|}
end_class

end_unit

