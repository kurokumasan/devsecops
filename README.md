# Team 1 deployment instrucment

```bash
kubectl create namespace test


cd container with pvc
kubectl apply –f pvc.yaml

cd ..

kubectl apply –f db_deployment.yaml
kubectl apply –f db_svc.yaml

kubectl apply –f shop_product_deployment.yaml
kubectl apply –f shop_product_svc.yaml

kubectl apply –f shop_order_deployment.yaml
kubectl apply –f shop_order_svc.yaml

kubectl apply -f ingress_nginx_controller.yaml
kubectl apply -f ingress.yaml

kubectl port-forward --namespace=ingress-nginx service/ingress-nginx-controller 8082:80
```

end point:
```
http://demo.localdev.me:8082
```

