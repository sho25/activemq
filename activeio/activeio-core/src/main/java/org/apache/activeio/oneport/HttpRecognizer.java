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
name|activeio
operator|.
name|oneport
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
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_class
specifier|public
class|class
name|HttpRecognizer
implements|implements
name|ProtocolRecognizer
block|{
specifier|static
specifier|private
name|HashSet
name|methods
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
static|static
block|{
comment|// This list built using: http://www.w3.org/Protocols/HTTP/Methods.html
name|methods
operator|.
name|add
argument_list|(
literal|"GET "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"PUT "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"POST "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"HEAD "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"LINK "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"TRACE "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"UNLINK "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"SEARCH "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"DELETE "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"CHECKIN "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"OPTIONS "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"CONNECT "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"CHECKOUT "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"SPACEJUMP "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"SHOWMETHOD "
argument_list|)
expr_stmt|;
name|methods
operator|.
name|add
argument_list|(
literal|"TEXTSEARCH "
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|final
specifier|public
name|HttpRecognizer
name|HTTP_RECOGNIZER
init|=
operator|new
name|HttpRecognizer
argument_list|()
decl_stmt|;
specifier|private
name|HttpRecognizer
parameter_list|()
block|{}
specifier|public
name|boolean
name|recognizes
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|(
literal|12
argument_list|)
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
literal|11
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
operator|(
name|char
operator|)
name|packet
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
name|b
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|(
name|char
operator|)
name|c
operator|)
operator|==
literal|' '
condition|)
break|break;
block|}
return|return
name|methods
operator|.
name|contains
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

