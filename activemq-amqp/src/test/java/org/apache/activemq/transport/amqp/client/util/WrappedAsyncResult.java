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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Base class used to wrap one AsyncResult with another.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WrappedAsyncResult
implements|implements
name|AsyncResult
block|{
specifier|protected
specifier|final
name|AsyncResult
name|wrapped
decl_stmt|;
comment|/**      * Create a new WrappedAsyncResult for the target AsyncResult      */
specifier|public
name|WrappedAsyncResult
parameter_list|(
name|AsyncResult
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|result
parameter_list|)
block|{
if|if
condition|(
name|wrapped
operator|!=
literal|null
condition|)
block|{
name|wrapped
operator|.
name|onFailure
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|()
block|{
if|if
condition|(
name|wrapped
operator|!=
literal|null
condition|)
block|{
name|wrapped
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
if|if
condition|(
name|wrapped
operator|!=
literal|null
condition|)
block|{
return|return
name|wrapped
operator|.
name|isComplete
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|AsyncResult
name|getWrappedRequest
parameter_list|()
block|{
return|return
name|wrapped
return|;
block|}
block|}
end_class

end_unit

