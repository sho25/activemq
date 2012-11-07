begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|Message
import|;
end_import

begin_comment
comment|/**  * NonCached version of the MessageEvaluationContext  *   *   */
end_comment

begin_class
specifier|public
class|class
name|NonCachedMessageEvaluationContext
extends|extends
name|MessageEvaluationContext
block|{
specifier|public
name|Message
name|getMessage
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|messageReference
operator|!=
literal|null
condition|?
name|messageReference
operator|.
name|getMessage
argument_list|()
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|setMessageReference
parameter_list|(
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|this
operator|.
name|messageReference
operator|=
name|messageReference
expr_stmt|;
block|}
specifier|protected
name|void
name|clearMessageCache
parameter_list|()
block|{     }
block|}
end_class

end_unit

