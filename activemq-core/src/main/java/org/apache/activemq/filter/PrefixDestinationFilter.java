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
name|filter
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
name|ActiveMQDestination
import|;
end_import

begin_comment
comment|/**  * Matches messages which match a prefix like "A.B.>"  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|PrefixDestinationFilter
extends|extends
name|DestinationFilter
block|{
specifier|private
name|String
index|[]
name|prefixes
decl_stmt|;
comment|/**      * An array of paths, the last path is '>'      *      * @param prefixes      */
specifier|public
name|PrefixDestinationFilter
parameter_list|(
name|String
index|[]
name|prefixes
parameter_list|)
block|{
name|this
operator|.
name|prefixes
operator|=
name|prefixes
expr_stmt|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
index|[]
name|path
init|=
name|DestinationPath
operator|.
name|getDestinationPaths
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|prefixes
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
operator|>=
name|length
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|length
operator|-
literal|1
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|prefixes
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|path
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|DestinationPath
operator|.
name|toString
argument_list|(
name|prefixes
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"[destination: "
operator|+
name|getText
argument_list|()
operator|+
literal|"]"
return|;
block|}
specifier|public
name|boolean
name|isWildcard
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

