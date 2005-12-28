begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
import|;
end_import

begin_comment
comment|/**  * An exception thrown when an operation is invoked on a service  * which has not yet been started.  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|NotStartedException
extends|extends
name|IllegalStateException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4907909323529887659L
decl_stmt|;
specifier|public
name|NotStartedException
parameter_list|()
block|{
name|super
argument_list|(
literal|"IllegalState: This service has not yet been started"
argument_list|,
literal|"AMQ-1003"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

