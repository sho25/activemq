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
name|openwire
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
name|Command
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * A @{link Comparator} of commands using their {@link Command#getCommandId()}  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CommandIdComparator
implements|implements
name|Comparator
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
assert|assert
name|o1
operator|instanceof
name|Command
assert|;
assert|assert
name|o2
operator|instanceof
name|Command
assert|;
name|Command
name|c1
init|=
operator|(
name|Command
operator|)
name|o1
decl_stmt|;
name|Command
name|c2
init|=
operator|(
name|Command
operator|)
name|o2
decl_stmt|;
return|return
name|c1
operator|.
name|getCommandId
argument_list|()
operator|-
name|c2
operator|.
name|getCommandId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

