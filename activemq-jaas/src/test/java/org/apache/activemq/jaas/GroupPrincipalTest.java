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
name|jaas
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
name|jaas
operator|.
name|GroupPrincipal
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Rev: $ $Date: $  */
end_comment

begin_class
specifier|public
class|class
name|GroupPrincipalTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testArguments
parameter_list|()
block|{
name|GroupPrincipal
name|principal
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"FOO"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FOO"
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|GroupPrincipal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ingore
parameter_list|)
block|{          }
block|}
specifier|public
name|void
name|testHash
parameter_list|()
block|{
name|GroupPrincipal
name|p1
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"FOO"
argument_list|)
decl_stmt|;
name|GroupPrincipal
name|p2
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"FOO"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|p1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|p1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|p1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|p2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|GroupPrincipal
name|p1
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"FOO"
argument_list|)
decl_stmt|;
name|GroupPrincipal
name|p2
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"FOO"
argument_list|)
decl_stmt|;
name|GroupPrincipal
name|p3
init|=
operator|new
name|GroupPrincipal
argument_list|(
literal|"BAR"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p1
operator|.
name|equals
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p1
operator|.
name|equals
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p1
operator|.
name|equals
argument_list|(
literal|"FOO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p1
operator|.
name|equals
argument_list|(
name|p3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

