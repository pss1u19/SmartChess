
Z
default_data_placeholderPlaceholder*$
shape:?????????*
dtype0
:
ConstConst*
valueB"????  *
dtype0
i
dense_3_dense_kernel
VariableV2*
shape:	?d*
shared_name *
dtype0*
	container 
<
Const_1Const*
valueB"  d   *
dtype0
D
Const_2Const*%
valueB	"               *
dtype0	
i
StatelessTruncatedNormalStatelessTruncatedNormalConst_1Const_2*
T0*
Tseed0	*
dtype0
8
Const_3Const*
valueB 2x?'>f??*
dtype0
=
CastCastConst_3*

SrcT0*
Truncate( *

DstT0
I
Init_dense_3_dense_kernelMulStatelessTruncatedNormalCast*
T0
?
Assign_dense_3_dense_kernelAssigndense_3_dense_kernelInit_dense_3_dense_kernel*
use_locking(*
T0*
validate_shape(
b
dense_3_dense_bias
VariableV2*
shape:d*
shared_name *
dtype0*
	container 
5
Const_4Const*
valueB:d*
dtype0
D
Const_5Const*%
valueB	"               *
dtype0	
e
StatelessRandomUniformStatelessRandomUniformConst_4Const_5*
T0*
Tseed0	*
dtype0
8
Const_6Const*
valueB 2Z/?4e??*
dtype0
?
Cast_1CastConst_6*

SrcT0*
Truncate( *

DstT0
G
Init_dense_3_dense_biasMulStatelessRandomUniformCast_1*
T0
?
Assign_dense_3_dense_biasAssigndense_3_dense_biasInit_dense_3_dense_bias*
use_locking(*
T0*
validate_shape(
h
dense_4_dense_kernel
VariableV2*
shape
:d2*
shared_name *
dtype0*
	container 
<
Const_7Const*
valueB"d   2   *
dtype0
D
Const_8Const*%
valueB	"               *
dtype0	
k
StatelessTruncatedNormal_1StatelessTruncatedNormalConst_7Const_8*
T0*
Tseed0	*
dtype0
8
Const_9Const*
valueB 2???B???*
dtype0
?
Cast_2CastConst_9*

SrcT0*
Truncate( *

DstT0
M
Init_dense_4_dense_kernelMulStatelessTruncatedNormal_1Cast_2*
T0
?
Assign_dense_4_dense_kernelAssigndense_4_dense_kernelInit_dense_4_dense_kernel*
use_locking(*
T0*
validate_shape(
b
dense_4_dense_bias
VariableV2*
shape:2*
shared_name *
dtype0*
	container 
6
Const_10Const*
valueB:2*
dtype0
E
Const_11Const*%
valueB	"               *
dtype0	
i
StatelessRandomUniform_1StatelessRandomUniformConst_10Const_11*
T0*
Tseed0	*
dtype0
9
Const_12Const*
valueB 2Jh??|Z??*
dtype0
@
Cast_3CastConst_12*

SrcT0*
Truncate( *

DstT0
I
Init_dense_4_dense_biasMulStatelessRandomUniform_1Cast_3*
T0
?
Assign_dense_4_dense_biasAssigndense_4_dense_biasInit_dense_4_dense_bias*
use_locking(*
T0*
validate_shape(
h
dense_5_dense_kernel
VariableV2*
shape
:2
*
shared_name *
dtype0*
	container 
=
Const_13Const*
valueB"2   
   *
dtype0
E
Const_14Const*%
valueB	"               *
dtype0	
m
StatelessTruncatedNormal_2StatelessTruncatedNormalConst_13Const_14*
T0*
Tseed0	*
dtype0
9
Const_15Const*
valueB 2?4)q??*
dtype0
@
Cast_4CastConst_15*

SrcT0*
Truncate( *

DstT0
M
Init_dense_5_dense_kernelMulStatelessTruncatedNormal_2Cast_4*
T0
?
Assign_dense_5_dense_kernelAssigndense_5_dense_kernelInit_dense_5_dense_kernel*
use_locking(*
T0*
validate_shape(
b
dense_5_dense_bias
VariableV2*
shape:
*
shared_name *
dtype0*
	container 
6
Const_16Const*
valueB:
*
dtype0
E
Const_17Const*%
valueB	"               *
dtype0	
i
StatelessRandomUniform_2StatelessRandomUniformConst_16Const_17*
T0*
Tseed0	*
dtype0
9
Const_18Const*
valueB 2"
???+??*
dtype0
@
Cast_5CastConst_18*

SrcT0*
Truncate( *

DstT0
I
Init_dense_5_dense_biasMulStatelessRandomUniform_2Cast_5*
T0
?
Assign_dense_5_dense_biasAssigndense_5_dense_biasInit_dense_5_dense_bias*
use_locking(*
T0*
validate_shape(
6
PlaceholderPlaceholder*
shape:*
dtype0
7
numberOfLossesPlaceholder*
shape: *
dtype0
1
trainingPlaceholder*
shape: *
dtype0

J
ReshapeReshapedefault_data_placeholderConst*
T0*
Tshape0
^
MatMulMatMulReshapedense_3_dense_kernel*
transpose_b( *
T0*
transpose_a( 
/
AddAddMatMuldense_3_dense_bias*
T0

ReluReluAdd*
T0
-
Activation_dense_3IdentityRelu*
T0
k
MatMul_1MatMulActivation_dense_3dense_4_dense_kernel*
transpose_b( *
T0*
transpose_a( 
3
Add_1AddMatMul_1dense_4_dense_bias*
T0

Relu_1ReluAdd_1*
T0
/
Activation_dense_4IdentityRelu_1*
T0
k
MatMul_2MatMulActivation_dense_4dense_5_dense_kernel*
transpose_b( *
T0*
transpose_a( 
3
Add_2AddMatMul_2dense_5_dense_bias*
T0

Relu6Relu6Add_2*
T0
.
Activation_dense_5IdentityRelu6*
T0
P
SquaredDifferenceSquaredDifferenceActivation_dense_5Placeholder*
T0
;
Const_19Const*
valueB :
?????????*
dtype0
O
MeanMeanSquaredDifferenceConst_19*

Tidx0*
	keep_dims( *
T0
2
Const_20Const*
value	B : *
dtype0

RankRankMean*
T0
2
Const_21Const*
value	B :*
dtype0
4
RangeRangeConst_20RankConst_21*

Tidx0
C
	ReduceSumSumMeanRange*

Tidx0*
	keep_dims( *
T0
2
Const_22Const*
value	B : *
dtype0
*
Rank_1RankSquaredDifference*
T0
2
Const_23Const*
value	B :*
dtype0
8
Range_1RangeConst_22Rank_1Const_23*

Tidx0
T
ReduceSum_1SumSquaredDifferenceRange_1*

Tidx0*
	keep_dims( *
T0
:
DivNoNanDivNoNanReduceSum_1numberOfLosses*
T0
4
default_training_lossIdentityDivNoNan*
T0
>
Gradients/OnesLikeOnesLikedefault_training_loss*
T0
;
Gradients/IdentityIdentityGradients/OnesLike*
T0
K
Gradients/DivNoNanDivNoNanGradients/IdentitynumberOfLosses*
T0
-
Gradients/NegateNegReduceSum_1*
T0
K
Gradients/DivNoNan_1DivNoNanGradients/NegatenumberOfLosses*
T0
O
Gradients/DivNoNan_2DivNoNanGradients/DivNoNan_1numberOfLosses*
T0
L
Gradients/MultiplyMulGradients/IdentityGradients/DivNoNan_2*
T0
>
Gradients/ShapeShapeReduceSum_1*
T0*
out_type0
C
Gradients/Shape_1ShapenumberOfLosses*
T0*
out_type0
e
Gradients/BroadcastGradientArgsBroadcastGradientArgsGradients/ShapeGradients/Shape_1*
T0
o
Gradients/SumSumGradients/DivNoNanGradients/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
S
Gradients/ReshapeReshapeGradients/SumGradients/Shape*
T0*
Tshape0
s
Gradients/Sum_1SumGradients/Multiply!Gradients/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_1ReshapeGradients/Sum_1Gradients/Shape_1*
T0*
Tshape0
F
Gradients/Shape_2ShapeSquaredDifference*
T0*
out_type0
9
Gradients/ConstConst*
value	B : *
dtype0
;
Gradients/Const_1Const*
value	B :*
dtype0
B
Gradients/SizeSizeGradients/Shape_2*
T0*
out_type0
6
Gradients/AddAddRange_1Gradients/Size*
T0
<
Gradients/ModModGradients/AddGradients/Size*
T0
X
Gradients/RangeRangeGradients/ConstGradients/SizeGradients/Const_1*

Tidx0
8
Gradients/OnesLike_1OnesLikeGradients/Mod*
T0
?
Gradients/DynamicStitchDynamicStitchGradients/RangeGradients/ModGradients/Shape_2Gradients/OnesLike_1*
T0*
N
;
Gradients/Const_2Const*
value	B :*
dtype0
Q
Gradients/MaximumMaximumGradients/DynamicStitchGradients/Const_2*
T0
C
Gradients/DivDivGradients/Shape_2Gradients/Maximum*
T0
a
Gradients/Reshape_2ReshapeGradients/ReshapeGradients/DynamicStitch*
T0*
Tshape0
U
Gradients/TileTileGradients/Reshape_2Gradients/Div*

Tmultiples0*
T0
;
Gradients/Const_3Const*
value	B :*
dtype0
Q
Gradients/CastCastGradients/Const_3*

SrcT0*
Truncate( *

DstT0
C
Gradients/SubtractSubActivation_dense_5Placeholder*
T0
H
Gradients/Multiply_1MulGradients/CastGradients/Subtract*
T0
J
Gradients/Multiply_2MulGradients/TileGradients/Multiply_1*
T0
8
Gradients/Negate_1NegGradients/Multiply_2*
T0
G
Gradients/Shape_3ShapeActivation_dense_5*
T0*
out_type0
@
Gradients/Shape_4ShapePlaceholder*
T0*
out_type0
i
!Gradients/BroadcastGradientArgs_1BroadcastGradientArgsGradients/Shape_3Gradients/Shape_4*
T0
u
Gradients/Sum_2SumGradients/Multiply_2!Gradients/BroadcastGradientArgs_1*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_3ReshapeGradients/Sum_2Gradients/Shape_3*
T0*
Tshape0
u
Gradients/Sum_3SumGradients/Negate_1#Gradients/BroadcastGradientArgs_1:1*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_4ReshapeGradients/Sum_3Gradients/Shape_4*
T0*
Tshape0
>
Gradients/Identity_1IdentityGradients/Reshape_3*
T0
F
Gradients/Relu6Grad	Relu6GradGradients/Identity_1Add_2*
T0
>
Gradients/Identity_2IdentityGradients/Relu6Grad*
T0
>
Gradients/Identity_3IdentityGradients/Relu6Grad*
T0
=
Gradients/Shape_5ShapeMatMul_2*
T0*
out_type0
G
Gradients/Shape_6Shapedense_5_dense_bias*
T0*
out_type0
i
!Gradients/BroadcastGradientArgs_2BroadcastGradientArgsGradients/Shape_5Gradients/Shape_6*
T0
u
Gradients/Sum_4SumGradients/Identity_2!Gradients/BroadcastGradientArgs_2*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_5ReshapeGradients/Sum_4Gradients/Shape_5*
T0*
Tshape0
w
Gradients/Sum_5SumGradients/Identity_3#Gradients/BroadcastGradientArgs_2:1*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_6ReshapeGradients/Sum_5Gradients/Shape_6*
T0*
Tshape0
t
Gradients/MatMulMatMulGradients/Reshape_5dense_5_dense_kernel*
transpose_b(*
T0*
transpose_a( 
t
Gradients/MatMul_1MatMulActivation_dense_4Gradients/Reshape_5*
transpose_b( *
T0*
transpose_a(
;
Gradients/Identity_4IdentityGradients/MatMul*
T0
D
Gradients/ReluGradReluGradGradients/Identity_4Add_1*
T0
=
Gradients/Identity_5IdentityGradients/ReluGrad*
T0
=
Gradients/Identity_6IdentityGradients/ReluGrad*
T0
=
Gradients/Shape_7ShapeMatMul_1*
T0*
out_type0
G
Gradients/Shape_8Shapedense_4_dense_bias*
T0*
out_type0
i
!Gradients/BroadcastGradientArgs_3BroadcastGradientArgsGradients/Shape_7Gradients/Shape_8*
T0
u
Gradients/Sum_6SumGradients/Identity_5!Gradients/BroadcastGradientArgs_3*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_7ReshapeGradients/Sum_6Gradients/Shape_7*
T0*
Tshape0
w
Gradients/Sum_7SumGradients/Identity_6#Gradients/BroadcastGradientArgs_3:1*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_8ReshapeGradients/Sum_7Gradients/Shape_8*
T0*
Tshape0
v
Gradients/MatMul_2MatMulGradients/Reshape_7dense_4_dense_kernel*
transpose_b(*
T0*
transpose_a( 
t
Gradients/MatMul_3MatMulActivation_dense_3Gradients/Reshape_7*
transpose_b( *
T0*
transpose_a(
=
Gradients/Identity_7IdentityGradients/MatMul_2*
T0
D
Gradients/ReluGrad_1ReluGradGradients/Identity_7Add*
T0
?
Gradients/Identity_8IdentityGradients/ReluGrad_1*
T0
?
Gradients/Identity_9IdentityGradients/ReluGrad_1*
T0
;
Gradients/Shape_9ShapeMatMul*
T0*
out_type0
H
Gradients/Shape_10Shapedense_3_dense_bias*
T0*
out_type0
j
!Gradients/BroadcastGradientArgs_4BroadcastGradientArgsGradients/Shape_9Gradients/Shape_10*
T0
u
Gradients/Sum_8SumGradients/Identity_8!Gradients/BroadcastGradientArgs_4*

Tidx0*
	keep_dims( *
T0
Y
Gradients/Reshape_9ReshapeGradients/Sum_8Gradients/Shape_9*
T0*
Tshape0
w
Gradients/Sum_9SumGradients/Identity_9#Gradients/BroadcastGradientArgs_4:1*

Tidx0*
	keep_dims( *
T0
[
Gradients/Reshape_10ReshapeGradients/Sum_9Gradients/Shape_10*
T0*
Tshape0
v
Gradients/MatMul_4MatMulGradients/Reshape_9dense_3_dense_kernel*
transpose_b(*
T0*
transpose_a( 
i
Gradients/MatMul_5MatMulReshapeGradients/Reshape_9*
transpose_b( *
T0*
transpose_a(
=
ShapeShapedense_3_dense_kernel*
T0*
out_type0
5
Const_24Const*
valueB
 *    *
dtype0
Y
%Init_optimizer_dense_3_dense_kernel-mFillShapeConst_24*
T0*

index_type0
u
 optimizer_dense_3_dense_kernel-m
VariableV2*
shape:	?d*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_3_dense_kernel-mAssign optimizer_dense_3_dense_kernel-m%Init_optimizer_dense_3_dense_kernel-m*
use_locking(*
T0*
validate_shape(
?
Shape_1Shapedense_3_dense_kernel*
T0*
out_type0
5
Const_25Const*
valueB
 *    *
dtype0
[
%Init_optimizer_dense_3_dense_kernel-vFillShape_1Const_25*
T0*

index_type0
u
 optimizer_dense_3_dense_kernel-v
VariableV2*
shape:	?d*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_3_dense_kernel-vAssign optimizer_dense_3_dense_kernel-v%Init_optimizer_dense_3_dense_kernel-v*
use_locking(*
T0*
validate_shape(
=
Shape_2Shapedense_3_dense_bias*
T0*
out_type0
5
Const_26Const*
valueB
 *    *
dtype0
Y
#Init_optimizer_dense_3_dense_bias-mFillShape_2Const_26*
T0*

index_type0
n
optimizer_dense_3_dense_bias-m
VariableV2*
shape:d*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_3_dense_bias-mAssignoptimizer_dense_3_dense_bias-m#Init_optimizer_dense_3_dense_bias-m*
use_locking(*
T0*
validate_shape(
=
Shape_3Shapedense_3_dense_bias*
T0*
out_type0
5
Const_27Const*
valueB
 *    *
dtype0
Y
#Init_optimizer_dense_3_dense_bias-vFillShape_3Const_27*
T0*

index_type0
n
optimizer_dense_3_dense_bias-v
VariableV2*
shape:d*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_3_dense_bias-vAssignoptimizer_dense_3_dense_bias-v#Init_optimizer_dense_3_dense_bias-v*
use_locking(*
T0*
validate_shape(
?
Shape_4Shapedense_4_dense_kernel*
T0*
out_type0
5
Const_28Const*
valueB
 *    *
dtype0
[
%Init_optimizer_dense_4_dense_kernel-mFillShape_4Const_28*
T0*

index_type0
t
 optimizer_dense_4_dense_kernel-m
VariableV2*
shape
:d2*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_4_dense_kernel-mAssign optimizer_dense_4_dense_kernel-m%Init_optimizer_dense_4_dense_kernel-m*
use_locking(*
T0*
validate_shape(
?
Shape_5Shapedense_4_dense_kernel*
T0*
out_type0
5
Const_29Const*
valueB
 *    *
dtype0
[
%Init_optimizer_dense_4_dense_kernel-vFillShape_5Const_29*
T0*

index_type0
t
 optimizer_dense_4_dense_kernel-v
VariableV2*
shape
:d2*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_4_dense_kernel-vAssign optimizer_dense_4_dense_kernel-v%Init_optimizer_dense_4_dense_kernel-v*
use_locking(*
T0*
validate_shape(
=
Shape_6Shapedense_4_dense_bias*
T0*
out_type0
5
Const_30Const*
valueB
 *    *
dtype0
Y
#Init_optimizer_dense_4_dense_bias-mFillShape_6Const_30*
T0*

index_type0
n
optimizer_dense_4_dense_bias-m
VariableV2*
shape:2*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_4_dense_bias-mAssignoptimizer_dense_4_dense_bias-m#Init_optimizer_dense_4_dense_bias-m*
use_locking(*
T0*
validate_shape(
=
Shape_7Shapedense_4_dense_bias*
T0*
out_type0
5
Const_31Const*
valueB
 *    *
dtype0
Y
#Init_optimizer_dense_4_dense_bias-vFillShape_7Const_31*
T0*

index_type0
n
optimizer_dense_4_dense_bias-v
VariableV2*
shape:2*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_4_dense_bias-vAssignoptimizer_dense_4_dense_bias-v#Init_optimizer_dense_4_dense_bias-v*
use_locking(*
T0*
validate_shape(
?
Shape_8Shapedense_5_dense_kernel*
T0*
out_type0
5
Const_32Const*
valueB
 *    *
dtype0
[
%Init_optimizer_dense_5_dense_kernel-mFillShape_8Const_32*
T0*

index_type0
t
 optimizer_dense_5_dense_kernel-m
VariableV2*
shape
:2
*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_5_dense_kernel-mAssign optimizer_dense_5_dense_kernel-m%Init_optimizer_dense_5_dense_kernel-m*
use_locking(*
T0*
validate_shape(
?
Shape_9Shapedense_5_dense_kernel*
T0*
out_type0
5
Const_33Const*
valueB
 *    *
dtype0
[
%Init_optimizer_dense_5_dense_kernel-vFillShape_9Const_33*
T0*

index_type0
t
 optimizer_dense_5_dense_kernel-v
VariableV2*
shape
:2
*
shared_name *
dtype0*
	container 
?
'Assign_optimizer_dense_5_dense_kernel-vAssign optimizer_dense_5_dense_kernel-v%Init_optimizer_dense_5_dense_kernel-v*
use_locking(*
T0*
validate_shape(
>
Shape_10Shapedense_5_dense_bias*
T0*
out_type0
5
Const_34Const*
valueB
 *    *
dtype0
Z
#Init_optimizer_dense_5_dense_bias-mFillShape_10Const_34*
T0*

index_type0
n
optimizer_dense_5_dense_bias-m
VariableV2*
shape:
*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_5_dense_bias-mAssignoptimizer_dense_5_dense_bias-m#Init_optimizer_dense_5_dense_bias-m*
use_locking(*
T0*
validate_shape(
>
Shape_11Shapedense_5_dense_bias*
T0*
out_type0
5
Const_35Const*
valueB
 *    *
dtype0
Z
#Init_optimizer_dense_5_dense_bias-vFillShape_11Const_35*
T0*

index_type0
n
optimizer_dense_5_dense_bias-v
VariableV2*
shape:
*
shared_name *
dtype0*
	container 
?
%Assign_optimizer_dense_5_dense_bias-vAssignoptimizer_dense_5_dense_bias-v#Init_optimizer_dense_5_dense_bias-v*
use_locking(*
T0*
validate_shape(
a
optimizer_beta1_power
VariableV2*
shape: *
shared_name *
dtype0*
	container 
G
Init_optimizer_beta1_powerConst*
valueB
 *fff?*
dtype0
?
Assign_optimizer_beta1_powerAssignoptimizer_beta1_powerInit_optimizer_beta1_power*
use_locking(*
T0*
validate_shape(
a
optimizer_beta2_power
VariableV2*
shape: *
shared_name *
dtype0*
	container 
G
Init_optimizer_beta2_powerConst*
valueB
 *w??*
dtype0
?
Assign_optimizer_beta2_powerAssignoptimizer_beta2_powerInit_optimizer_beta2_power*
use_locking(*
T0*
validate_shape(
5
Const_36Const*
valueB
 *fff?*
dtype0
5
Const_37Const*
valueB
 *w??*
dtype0
5
Const_38Const*
valueB
 *o?:*
dtype0
5
Const_39Const*
valueB
 *???3*
dtype0
?
	ApplyAdam	ApplyAdamdense_3_dense_kernel optimizer_dense_3_dense_kernel-m optimizer_dense_3_dense_kernel-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/MatMul_5*
use_locking(*
T0*
use_nesterov( 
?
ApplyAdam_1	ApplyAdamdense_3_dense_biasoptimizer_dense_3_dense_bias-moptimizer_dense_3_dense_bias-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/Reshape_10*
use_locking(*
T0*
use_nesterov( 
?
ApplyAdam_2	ApplyAdamdense_4_dense_kernel optimizer_dense_4_dense_kernel-m optimizer_dense_4_dense_kernel-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/MatMul_3*
use_locking(*
T0*
use_nesterov( 
?
ApplyAdam_3	ApplyAdamdense_4_dense_biasoptimizer_dense_4_dense_bias-moptimizer_dense_4_dense_bias-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/Reshape_8*
use_locking(*
T0*
use_nesterov( 
?
ApplyAdam_4	ApplyAdamdense_5_dense_kernel optimizer_dense_5_dense_kernel-m optimizer_dense_5_dense_kernel-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/MatMul_1*
use_locking(*
T0*
use_nesterov( 
?
ApplyAdam_5	ApplyAdamdense_5_dense_biasoptimizer_dense_5_dense_bias-moptimizer_dense_5_dense_bias-voptimizer_beta1_poweroptimizer_beta2_powerConst_38Const_36Const_37Const_39Gradients/Reshape_6*
use_locking(*
T0*
use_nesterov( 
4
MulMuloptimizer_beta1_powerConst_36*
T0
^
AssignAssignoptimizer_beta1_powerMul*
use_locking(*
T0*
validate_shape(
6
Mul_1Muloptimizer_beta2_powerConst_37*
T0
b
Assign_1Assignoptimizer_beta2_powerMul_1*
use_locking(*
T0*
validate_shape(
7
default_outputIdentityActivation_dense_5*
T0
2
Const_40Const*
value	B :*
dtype0
R
ArgMaxArgMaxdefault_outputConst_40*

Tidx0*
T0*
output_type0	
2
Const_41Const*
value	B :*
dtype0
Q
ArgMax_1ArgMaxPlaceholderConst_41*

Tidx0*
T0*
output_type0	
I
EqualEqualArgMaxArgMax_1*
incompatible_shape_error(*
T0	
=
Cast_6CastEqual*

SrcT0
*
Truncate( *

DstT0
2
Const_42Const*
value	B : *
dtype0
F
Mean_1MeanCast_6Const_42*

Tidx0*
	keep_dims( *
T0 "?