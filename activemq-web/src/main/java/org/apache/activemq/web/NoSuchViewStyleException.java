begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|web
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|NoSuchViewStyleException
extends|extends
name|ServletException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3590398087507019767L
decl_stmt|;
specifier|private
specifier|final
name|String
name|style
decl_stmt|;
specifier|public
name|NoSuchViewStyleException
parameter_list|(
name|String
name|style
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
literal|"The view style '"
operator|+
name|style
operator|+
literal|"' could not be created"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|style
operator|=
name|style
expr_stmt|;
block|}
specifier|public
name|String
name|getStyle
parameter_list|()
block|{
return|return
name|style
return|;
block|}
block|}
end_class

end_unit

