begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005 Gianny Damour.  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|xnet
operator|.
name|hba
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Inet6Address
import|;
end_import

begin_comment
comment|/**  * @version $Revision$ $Date$  */
end_comment

begin_class
specifier|public
class|class
name|ExactIPv6AddressPermission
implements|implements
name|IPAddressPermission
block|{
specifier|private
specifier|static
specifier|final
name|Pattern
name|MASK_VALIDATOR
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(([a-fA-F0-9]{1,4}:){7})([a-fA-F0-9]{1,4})$"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|canSupport
parameter_list|(
name|String
name|mask
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|MASK_VALIDATOR
operator|.
name|matcher
argument_list|(
name|mask
argument_list|)
decl_stmt|;
return|return
name|matcher
operator|.
name|matches
argument_list|()
return|;
block|}
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
specifier|public
name|ExactIPv6AddressPermission
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
specifier|public
name|ExactIPv6AddressPermission
parameter_list|(
name|String
name|mask
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|MASK_VALIDATOR
operator|.
name|matcher
argument_list|(
name|mask
argument_list|)
decl_stmt|;
if|if
condition|(
literal|false
operator|==
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Mask "
operator|+
name|mask
operator|+
literal|" does not match pattern "
operator|+
name|MASK_VALIDATOR
operator|.
name|pattern
argument_list|()
argument_list|)
throw|;
block|}
name|bytes
operator|=
operator|new
name|byte
index|[
literal|16
index|]
expr_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|mask
argument_list|,
literal|":"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|bytes
index|[
name|pos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|value
operator|&
literal|0xff00
operator|)
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|bytes
index|[
name|pos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|implies
parameter_list|(
name|InetAddress
name|address
parameter_list|)
block|{
if|if
condition|(
literal|false
operator|==
name|address
operator|instanceof
name|Inet6Address
condition|)
block|{
return|return
literal|false
return|;
block|}
name|byte
index|[]
name|byteAddress
init|=
name|address
operator|.
name|getAddress
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|16
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|byteAddress
index|[
name|i
index|]
operator|!=
name|bytes
index|[
name|i
index|]
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
block|}
end_class

end_unit

