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
name|maven
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|tool
operator|.
name|JmsProducerSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_comment
comment|/**  * Goal which touches a timestamp file.  *   * @goal producer  * @phase process  */
end_comment

begin_class
specifier|public
class|class
name|ProducerMojo
extends|extends
name|AbstractMojo
block|{
specifier|private
name|String
index|[]
name|validPrefix
init|=
block|{
literal|"sysTest."
block|,
literal|"factory."
block|,
literal|"producer."
block|,
literal|"tpSampler."
block|,
literal|"cpuSampler."
block|}
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|JmsProducerSystem
operator|.
name|main
argument_list|(
name|createArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
index|[]
name|createArgument
parameter_list|()
block|{
name|List
name|args
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Properties
name|sysProps
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|Set
name|keys
init|=
operator|new
name|HashSet
argument_list|(
name|sysProps
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|keys
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|isRecognizedProperty
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
name|key
operator|+
literal|"="
operator|+
name|sysProps
operator|.
name|remove
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|String
index|[]
operator|)
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|isRecognizedProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|validPrefix
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|validPrefix
index|[
name|j
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

