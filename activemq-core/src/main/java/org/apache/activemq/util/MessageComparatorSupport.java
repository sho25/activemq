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
name|util
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
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
comment|/**  * A base class for comparators which works on JMS {@link Message} objects  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageComparatorSupport
implements|implements
name|Comparator
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|object1
parameter_list|,
name|Object
name|object2
parameter_list|)
block|{
name|Message
name|command1
init|=
operator|(
name|Message
operator|)
name|object1
decl_stmt|;
name|Message
name|command2
init|=
operator|(
name|Message
operator|)
name|object2
decl_stmt|;
return|return
name|compareMessages
argument_list|(
name|command1
argument_list|,
name|command2
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|int
name|compareMessages
parameter_list|(
name|Message
name|message1
parameter_list|,
name|Message
name|message2
parameter_list|)
function_decl|;
specifier|protected
name|int
name|compareComparators
parameter_list|(
name|Comparable
name|comparable
parameter_list|,
name|Comparable
name|comparable2
parameter_list|)
block|{
if|if
condition|(
name|comparable
operator|!=
literal|null
condition|)
block|{
return|return
name|comparable
operator|.
name|compareTo
argument_list|(
name|comparable2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|comparable2
operator|!=
literal|null
condition|)
block|{
return|return
name|comparable2
operator|.
name|compareTo
argument_list|(
name|comparable
argument_list|)
operator|*
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

