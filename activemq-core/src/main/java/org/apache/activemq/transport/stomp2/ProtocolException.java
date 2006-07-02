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
name|transport
operator|.
name|stomp2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *   * @author<a href="http://hiramchirino.com">chirino</a>   */
end_comment

begin_class
specifier|public
class|class
name|ProtocolException
extends|extends
name|IOException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2869735532997332242L
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|fatal
decl_stmt|;
specifier|public
name|ProtocolException
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProtocolException
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProtocolException
parameter_list|(
name|String
name|s
parameter_list|,
name|boolean
name|fatal
parameter_list|)
block|{
name|this
argument_list|(
name|s
argument_list|,
name|fatal
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProtocolException
parameter_list|(
name|String
name|s
parameter_list|,
name|boolean
name|fatal
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|this
operator|.
name|fatal
operator|=
name|fatal
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFatal
parameter_list|()
block|{
return|return
name|fatal
return|;
block|}
block|}
end_class

end_unit

