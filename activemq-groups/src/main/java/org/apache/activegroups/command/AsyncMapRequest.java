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
name|activegroups
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Return information about map update  *   */
end_comment

begin_class
specifier|public
class|class
name|AsyncMapRequest
implements|implements
name|RequestCallback
block|{
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|requests
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|add
parameter_list|(
name|String
name|id
parameter_list|,
name|MapRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|setCallback
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|requests
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wait for requests      * @param timeout      * @return      */
specifier|public
name|boolean
name|isSuccess
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|long
name|deadline
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
while|while
condition|(
operator|!
name|this
operator|.
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|mutex
init|)
block|{
try|try
block|{
name|this
operator|.
name|mutex
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
break|break;
block|}
block|}
name|timeout
operator|=
name|Math
operator|.
name|max
argument_list|(
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|requests
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|void
name|finished
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|requests
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

