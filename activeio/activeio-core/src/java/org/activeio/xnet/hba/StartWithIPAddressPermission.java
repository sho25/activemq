begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Inet4Address
import|;
end_import

begin_comment
comment|/**  * @version $Revision$ $Date$  */
end_comment

begin_class
specifier|public
class|class
name|StartWithIPAddressPermission
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
literal|"^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.0$"
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
name|StartWithIPAddressPermission
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
name|Byte
index|[]
name|tmpBytes
init|=
operator|new
name|Byte
index|[
literal|4
index|]
decl_stmt|;
name|boolean
name|isWildCard
init|=
literal|false
decl_stmt|;
name|int
name|size
init|=
literal|0
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|String
name|group
init|=
name|matcher
operator|.
name|group
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
condition|)
block|{
name|isWildCard
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isWildCard
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"0 at position "
operator|+
name|size
operator|+
literal|" in mask"
argument_list|)
throw|;
block|}
else|else
block|{
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|<
literal|0
operator|||
literal|255
operator|<
name|value
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"byte #"
operator|+
name|i
operator|+
literal|" is not valid."
argument_list|)
throw|;
block|}
name|tmpBytes
index|[
name|i
index|]
operator|=
operator|new
name|Byte
argument_list|(
operator|(
name|byte
operator|)
name|value
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
block|}
name|bytes
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
name|tmpBytes
index|[
name|i
index|]
operator|.
name|byteValue
argument_list|()
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
name|Inet4Address
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
name|bytes
operator|.
name|length
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

