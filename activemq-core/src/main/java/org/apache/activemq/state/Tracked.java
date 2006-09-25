begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
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
name|command
operator|.
name|Response
import|;
end_import

begin_class
specifier|public
class|class
name|Tracked
extends|extends
name|Response
block|{
specifier|private
name|Runnable
name|runnable
decl_stmt|;
specifier|public
name|Tracked
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
block|}
specifier|public
name|void
name|onResponses
parameter_list|()
block|{
if|if
condition|(
name|runnable
operator|!=
literal|null
condition|)
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
name|runnable
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isWaitingForResponse
parameter_list|()
block|{
return|return
name|runnable
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

